package org.cvarda.transparencia.bolsafamilia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.cvarda.transparencia.bolsafamilia.BolsaFamiliaSaques.Sacador;

public class SacadoresBolsaFamilia {

	private final Map<String, Sacador> sacadores;

	SacadoresBolsaFamilia(Map<String, Sacador> sacadores) {
		this.sacadores = sacadores;
	}

	public int size() {
		return this.sacadores.size();
	}
	
	/**
	 * Retorna todos os sacadores.
	 * 
	 * @return todos os sacadores
	 */
	public Collection<Sacador> getTodosSacadores() {
		return this.sacadores.values();
	}
	
	/**
	 * Retorna somente os sacadores cujo total de saques (R$) seja igual ou maior que
	 * <code>threshold</code>. A lista é retornada em ordem decrescente de valor total.
	 * 
	 * @param threshold valor de corte (R$)
	 * @return
	 */
	public List<Sacador> getSaquesMaioresQue(double threshold) {
		List<Sacador> maiores = new ArrayList<BolsaFamiliaSaques.Sacador>();
		
		for (Sacador dadosSaque : this.sacadores.values()) {
			if (dadosSaque.getTotalSaques() >= threshold) {
				maiores.add(dadosSaque);
			}
		}
		
		// ordena por valor em ordem crescente
		Collections.sort(maiores, new Comparator<Sacador>() {
			@Override
			public int compare(Sacador o1, Sacador o2) {
				return (int) (o2.getTotalSaques() - o1.getTotalSaques());
			}
		});
		
		return maiores;
	}
}
