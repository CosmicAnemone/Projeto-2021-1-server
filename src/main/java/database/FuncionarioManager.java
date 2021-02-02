package database;

import com.mongodb.client.model.Updates;
import data.Empresa;
import data.Empresa.Beneficio;
import data.Field;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Objects;

public class FuncionarioManager {
	public static String addBeneficio(long CPF, String nomeEmpresa, String nomeBeneficio, Document campos) {
		try {
			Empresa empresa = Objects.requireNonNull(EmpresaManager.getEmpresa(nomeEmpresa),
					"empresa inexistente: " + nomeEmpresa);
			Beneficio beneficio = Objects.requireNonNull(empresa.beneficios.get(nomeBeneficio),
					"benefício inexistente ou não oferecido pela empresa em questão: " + nomeBeneficio);
			if (beneficio.campos.size() != campos.size())
				return "quantidade de campos fornecida diferente da quantidade necessária";
			ArrayList<Bson> updates = new ArrayList<>(campos.size() + 2);
			for (String nome : beneficio.campos.keySet()) {
				if (campos.containsKey(nome)) {
					try {
						switch (beneficio.campos.get(nome)) {
							case Field.campo_string -> updates.add(Updates.set(
									Field.empresas + "." + nomeEmpresa + "." + Field.campos,
									Objects.requireNonNull(campos.getString(nome), "campo inexistente: " + nome)
							));
							case Field.campo_inteiro -> updates.add(Updates.set(
									Field.empresas + "." + nomeEmpresa + "." + Field.campos,
									Objects.requireNonNull(campos.getInteger(nome), "campo inexistente: " + nome)
							));
							case Field.campo_decimal -> updates.add(Updates.set(
									Field.empresas + "." + nomeEmpresa + "." + Field.campos,
									Objects.requireNonNull(campos.getDouble(nome), "campo inexistente: " + nome)
							));
							default -> {
								return "tipo de campo inválido no benefício: " + beneficio.campos.get(nome);
							}
						}
					} catch (ClassCastException e) {
						return "tipo de campo inválido na request. Nome do campo: " + nome;
					}
				} else {
					return "campo necessário não presente: " + nome;
				}
			}
			return DatabaseManager.addBeneficio(CPF, nomeEmpresa, nomeBeneficio, updates)
					? null
					: "falha no acesso ao banco de dados";
		} catch (NullPointerException e) {
			return e.getMessage();
		}
	}
	
	public static String editBeneficio(long CPF, String nomeEmpresa, String nomeBeneficio, Document campos) {
		try {
			Empresa empresa = Objects.requireNonNull(EmpresaManager.getEmpresa(nomeEmpresa),
					"empresa inexistente: " + nomeEmpresa);
			Beneficio beneficio = Objects.requireNonNull(empresa.beneficios.get(nomeBeneficio),
					"benefício inexistente ou não oferecido pela empresa em questão: " + nomeBeneficio);
			if (beneficio.campos.size() < campos.size())
				return "quantidade de campos fornecida diferente da quantidade necessária";
			ArrayList<Bson> updates = new ArrayList<>(campos.size());
			for (String nome : campos.keySet()) {
				if (beneficio.campos.containsKey(nome)) {
					try{
					switch (beneficio.campos.get(nome)) {
						case Field.campo_string -> updates.add(Updates.set(
								Field.empresas + "." + nomeEmpresa + "." + Field.campos,
								Objects.requireNonNull(campos.getString(nome), "campo inexistente: " + nome)
						));
						case Field.campo_inteiro -> updates.add(Updates.set(
								Field.empresas + "." + nomeEmpresa + "." + Field.campos,
								Objects.requireNonNull(campos.getInteger(nome), "campo inexistente: " + nome)
						));
						case Field.campo_decimal -> updates.add(Updates.set(
								Field.empresas + "." + nomeEmpresa + "." + Field.campos,
								Objects.requireNonNull(campos.getDouble(nome), "campo inexistente: " + nome)
						));
						default -> {
							return "tipo de campo inválido no benefício: " + beneficio.campos.get(nome);
						}
					}
				} catch (ClassCastException e) {
					return "tipo de campo inválido na request. Nome do campo: " + nome;
				}
				} else {
					return "campo necessário não presente: " + nome;
				}
			}
			return DatabaseManager.editBeneficio(CPF, nomeEmpresa, nomeBeneficio, updates)
					? null
					: "falha no acesso ao banco de dados";
		} catch (NullPointerException e) {
			return e.getMessage();
		}
	}
	
	public static String removeBeneficio(long CPF, String nomeEmpresa, String nomeBeneficio) {
		return DatabaseManager.removeBeneficio(CPF, nomeEmpresa, nomeBeneficio)
				? null
				: "falha no acesso ao banco de dados";
	}
}
