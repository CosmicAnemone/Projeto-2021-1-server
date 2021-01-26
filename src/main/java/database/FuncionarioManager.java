package database;

import ch.qos.logback.classic.Logger;
import com.mongodb.client.model.Updates;
import data.Empresa;
import data.Empresa.Beneficio;
import data.Field;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;

public class FuncionarioManager {
	private static final Logger LOG = (Logger) LoggerFactory.getLogger(FuncionarioManager.class);
	
	public static boolean addBeneficio(long CPF, String nomeEmpresa, String nomeBeneficio, Document campos) {
		try {
			Empresa empresa = Objects.requireNonNull(EmpresaManager.getEmpresa(nomeEmpresa));
			Beneficio beneficio = Objects.requireNonNull(empresa.beneficios.get(nomeBeneficio));
			if (beneficio.campos.size() != campos.size()) return false;
			ArrayList<Bson> updates = new ArrayList<>(campos.size() + 2);
			for (String nome : beneficio.campos.keySet()) {
				if (campos.containsKey(nome)) {
					switch (beneficio.campos.get(nome)) {
						case Field.campo_string -> updates.add(Updates.set(
								Field.empresas + "." + nomeEmpresa + "." + Field.campos,
								Objects.requireNonNull(campos.getString(nome))
						));
						case Field.campo_inteiro -> updates.add(Updates.set(
								Field.empresas + "." + nomeEmpresa + "." + Field.campos,
								Objects.requireNonNull(campos.getInteger(nome))
						));
						case Field.campo_decimal -> updates.add(Updates.set(
								Field.empresas + "." + nomeEmpresa + "." + Field.campos,
								Objects.requireNonNull(campos.getDouble(nome))
						));
						default -> {
							LOG.error("Tipo de campo inválido em addBeneficio: " + beneficio.campos.get(nome));
							return false;
						}
					}
				} else {
					return false;
				}
			}
			return DatabaseManager.addBeneficio(CPF, nomeEmpresa, nomeBeneficio, updates);
		} catch (NullPointerException | ClassCastException e) {
			return false;
		}
	}
	
	public static boolean editBeneficio(long CPF, String nomeEmpresa, String nomeBeneficio, Document campos) {
		try {
			Empresa empresa = Objects.requireNonNull(EmpresaManager.getEmpresa(nomeEmpresa));
			Beneficio beneficio = Objects.requireNonNull(empresa.beneficios.get(nomeBeneficio));
			if (beneficio.campos.size() < campos.size()) return false;
			ArrayList<Bson> updates = new ArrayList<>(campos.size());
			for (String nome : campos.keySet()) {
				if (beneficio.campos.containsKey(nome)) {
					switch (beneficio.campos.get(nome)) {
						case Field.campo_string -> updates.add(Updates.set(
								Field.empresas + "." + nomeEmpresa + "." + Field.campos,
								Objects.requireNonNull(campos.getString(nome))
						));
						case Field.campo_inteiro -> updates.add(Updates.set(
								Field.empresas + "." + nomeEmpresa + "." + Field.campos,
								Objects.requireNonNull(campos.getInteger(nome))
						));
						case Field.campo_decimal -> updates.add(Updates.set(
								Field.empresas + "." + nomeEmpresa + "." + Field.campos,
								Objects.requireNonNull(campos.getDouble(nome))
						));
						default -> {
							LOG.error("Tipo de campo inválido em editBeneficio: " + beneficio.campos.get(nome));
							return false;
						}
					}
				} else {
					return false;
				}
			}
			return DatabaseManager.editBeneficio(CPF, nomeEmpresa, nomeBeneficio, updates);
		} catch (NullPointerException | ClassCastException e) {
			return false;
		}
	}
	
	public static boolean removeBeneficio(long CPF, String nomeEmpresa, String nomeBeneficio) {
		return DatabaseManager.removeBeneficio(CPF, nomeEmpresa, nomeBeneficio);
	}
}
