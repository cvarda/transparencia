package org.cvarda.transparencia.bolsafamilia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import org.cvarda.transparencia.TransparenciaConfig;
import org.cvarda.transparencia.util.ConsoleCounter;

/**
 * Efetua parse e realiza alguns filtros básicos sobre os resultados encontrados. Não é thread-safe.
 * 
 * @author cvarda
 *
 */
public class BolsaFamiliaSaques {
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private Map<String, Sacador> sacadores;
	
	private boolean headerPassed = false;

	public SacadoresBolsaFamilia parse(File file, String encoding) throws IOException, ParseException {
		try {
			ConsoleCounter cc = new ConsoleCounter(120, 50000);
			cc.setEnabled(TransparenciaConfig.OUTPUT_PARSING_PROGRESS);
			
			this.sacadores = new HashMap<String, Sacador>();
			
			BufferedReader in = null;
			
			try {
				InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
				in = new BufferedReader(reader);
				String line = null;
				while ((line = in.readLine()) != null) {
					this.parseLine(line);
					cc.add();
				}
			} finally {
				if (in != null) {
					in.close();
				}
				cc.done();
			}
			
			return new SacadoresBolsaFamilia(this.sacadores);
		} finally {
			this.sacadores = null;
			this.headerPassed = false;
		}
	}

	private Line parseLine(String stringLine) throws ParseException {
		if (!headerPassed && stringLine.startsWith("UF") && stringLine.endsWith("Data do Saque")) {
			headerPassed = true;
			return null;
		}
		Line line = new Line(stringLine);
		Sacador ds = this.getSacador(line.nis, line);
		ds.addPagamento(new Saque(line.dataSaque, line.mesReferencia, line.valor));
		return line;
	}
	
	private Sacador getSacador(String nis, Line fromLine) {
		Sacador dadosSaque = this.sacadores.get(nis);
		if (dadosSaque == null && fromLine != null) {
			dadosSaque = new Sacador();
			dadosSaque.nome = fromLine.nomeCidadao;
			dadosSaque.nis = fromLine.nis;
			dadosSaque.uf = fromLine.uf;
			dadosSaque.municipio = fromLine.municipio;
			
			this.sacadores.put(nis, dadosSaque);
		}
		return dadosSaque;
	}

	@ToString
	@Getter
	public static final class Sacador {
		private String nome;
		private String nis;
		private String uf;
		private String municipio;
		private double totalSaques = 0.0;
		private List<Saque> saques = new ArrayList<Saque>();
		
		private void addPagamento(Saque pagamento) {
			this.saques.add(pagamento);
			this.totalSaques += pagamento.valorSacado;
		}
	}
	
	@ToString
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class Saque {
		private Date dataSaque;
		private String descricao;
		private double valorSacado;
	}
	
	@ToString
	private final class Line {

		private String nis;
		private String nomeCidadao;
		private String uf;
		private String codigoSiafi;
		private String municipio;
		private String mesReferencia;
		private String parcela;
		private Date dataSaque;
		public double valor;

		public Line(String line) throws ParseException {
			String[] t = line.split("\\t");
			this.nis = t[7];
			this.nomeCidadao = t[8];
			this.uf = t[0];
			this.codigoSiafi = t[1];
			this.municipio = t[2];
			this.mesReferencia = t[10];
			this.parcela = t[12];
			this.dataSaque = sdf.parse(t[13]);
			String sValor = t[11];
			if (sValor.length() > 6) {
				sValor = sValor.replace(",", "");
			}
			this.valor = Double.parseDouble(sValor);
		}

	}
	
}
