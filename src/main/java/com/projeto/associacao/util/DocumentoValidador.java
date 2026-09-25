package com.projeto.associacao.util;

/**
 * Validação de CPF e CNPJ pelos dígitos verificadores (módulo 11).
 *
 * O CNPJ segue o novo padrão alfanumérico da Receita Federal: os 12
 * primeiros caracteres podem ser dígitos (0-9) ou letras maiúsculas (A-Z);
 * os 2 últimos continuam sendo dígitos verificadores numéricos. Cada
 * caractere é convertido para um valor numérico (dígito = o próprio valor;
 * letra = código ASCII - 48) antes de aplicar os pesos, exatamente como
 * descrito na Nota Técnica da RFB sobre o CNPJ alfanumérico.
 */
public final class DocumentoValidador {

	private static final int[] PESOS_CPF_1 = { 10, 9, 8, 7, 6, 5, 4, 3, 2 };
	private static final int[] PESOS_CPF_2 = { 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };
	private static final int[] PESOS_CNPJ_1 = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
	private static final int[] PESOS_CNPJ_2 = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

	private DocumentoValidador() {
	}

	public static boolean validarCpf(String cpf) {
		if (cpf == null) {
			return false;
		}
		String limpo = cpf.replaceAll("[^0-9]", "");
		if ((limpo.length() != 11) || limpo.chars().allMatch(c -> c == limpo.charAt(0))) {
			return false;
		}

		int[] digitos = limpo.chars().map(c -> c - '0').toArray();
		int dv1 = calcularDigitoVerificador(digitos, 9, PESOS_CPF_1);
		int dv2 = calcularDigitoVerificador(digitos, 10, PESOS_CPF_2);
		return (digitos[9] == dv1) && (digitos[10] == dv2);
	}

	public static boolean validarCnpj(String cnpj) {
		if (cnpj == null) {
			return false;
		}
		String limpo = cnpj.replaceAll("[^0-9A-Za-z]", "").toUpperCase();
		if ((limpo.length() != 14) || !limpo.matches("[0-9A-Z]{12}[0-9]{2}")
				|| limpo.chars().allMatch(c -> c == limpo.charAt(0))) {
			return false;
		}

		int[] valores = new int[14];
		for (int i = 0; i < 14; i++) {
			valores[i] = limpo.charAt(i) - '0';
		}

		int dv1 = calcularDigitoVerificador(valores, 12, PESOS_CNPJ_1);
		int dv2 = calcularDigitoVerificador(valores, 13, PESOS_CNPJ_2);
		return (valores[12] == dv1) && (valores[13] == dv2);
	}

	private static int calcularDigitoVerificador(int[] valores, int quantidadeCaracteres, int[] pesos) {
		int soma = 0;
		for (int i = 0; i < quantidadeCaracteres; i++) {
			soma += valores[i] * pesos[i];
		}
		int resto = soma % 11;
		return (resto < 2) ? 0 : (11 - resto);
	}
}
