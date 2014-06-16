package org.cvarda.transparencia.reports;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.cvarda.transparencia.TransparenciaConfig;
import org.cvarda.transparencia.bolsafamilia.BolsaFamiliaSaques;
import org.cvarda.transparencia.bolsafamilia.BolsaFamiliaSaques.Sacador;
import org.cvarda.transparencia.bolsafamilia.BolsaFamiliaSaques.Saque;
import org.cvarda.transparencia.bolsafamilia.SacadoresBolsaFamilia;
import org.cvarda.transparencia.util.RepeatedStrings;

/**
 * Executor de análise de saques do bolsa-família.
 * 
 * <p>Parâmetros:
 * <ul>
 * <li>nome do período que está sendo analisado (p.ex., "JANEIRO/2014") - se tiver espaços, deve estar entre aspas
 * <li>arquivo.csv: arquivo baixado de http://www.portaldatransparencia.gov.br/downloads/mensal.asp?c=BolsaFamiliaSacado
 * <li>output.txt: arquivo de saída do relatório (se omitido vai para System.out)
 * </ul>
 * 
 * @author cvarda
 *
 */
public class ReportBolsaFamiliaSaques {

	/**
	 * Gera um relatório de análise sobre um arquivo csv com informações sobre saques do bolsa-família de
	 * um determinado mês.
	 * 
	 * 
	 * @param threshold limite: o relatório irá detalhar as pessoas cujos saques no mês passarem deste valor
	 * @param periodo texto para descrever o período (ex. "Janeiro de 2014")
	 * @param inputFile arquivo csv com os dados de saques de um determinado mês
	 * @param out {@link PrintStream} para gravar o relatório
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void generate(double threshold, String periodo, File inputFile, PrintStream out) throws IOException, ParseException {
		TransparenciaConfig.OUTPUT_PARSING_PROGRESS = true;
		
		System.out.println("Lendo arquivo " + inputFile.getName() + " para gerar " + periodo);
		BolsaFamiliaSaques bfs = new BolsaFamiliaSaques();
		
		SacadoresBolsaFamilia sacadores = bfs.parse(inputFile, "ISO-8859-1");
		List<Sacador> maiores = sacadores.getSaquesMaioresQue(threshold);
		
		NumberFormat cf = NumberFormat.getCurrencyInstance();
		
		out.println("=== Análise de saques do Bolsa-Família ===========================");
		out.println("Período analisado: " + periodo);
		out.println("Sacadores no período: " + sacadores.size() + " pessoas");
		out.println("Pagamentos iguais ou superiores a " + cf.format(threshold) + ", em ordem decrescente do total por pessoa:");
		out.println(String.format("Sacaram mais de %s neste período: %s pessoa(s)", cf.format(threshold), maiores.size()));
		
		// escreve o relatório
		writeOut(out, maiores);

		out.println("---- Fim ---------------------------------------------------------");
		out.println("Fonte dos dados: http://www.portaldatransparencia.gov.br/downloads/mensal.asp?c=BolsaFamiliaSacado");
		out.println("Código-fonte do gerador desta análise: https://github.com/cvarda/transparencia");
		out.println("==================================================================");
		
		System.out.println("Fim da geração do relatório " + periodo);
		
		out.flush();
	}

	private static void writeOut(PrintStream out, List<Sacador> maiores) {
		final NumberFormat cf = NumberFormat.getCurrencyInstance();
		final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		for (Sacador dadosSaque : maiores) {
			out.println("-----------------------------------------");
			out.println(String.format("Total %s sacado por %s [NIS: %s], %s - %s", cf.format(dadosSaque.getTotalSaques()), dadosSaque.getNome(), dadosSaque.getNis(), dadosSaque.getMunicipio(), dadosSaque.getUf()));
			
			List<String> saques = new ArrayList<String>();
			
			for (Saque p : dadosSaque.getSaques()) {
				saques.add(df.format(p.getDataSaque()) + " -> " + cf.format(p.getValorSacado()));
			}
			
			saques = RepeatedStrings.cleanUp(saques);
			for (String string : saques) {
				out.println(string);
			}
		}
	}

}
