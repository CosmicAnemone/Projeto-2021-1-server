package data;

import org.bson.Document;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Funcionario {
	public final HashMap<String, Empresa> empresas;
	
	private Funcionario(HashMap<String, Empresa> empresas) {
		this.empresas = empresas;
	}
	
	public static Funcionario load(Document document) {
		try {
			HashMap<String, Empresa> empresas = new HashMap<>();
			Document empresasDoc = Objects.requireNonNull(document.get(Field.empresas, Document.class));
			for(String nome:empresasDoc.keySet()){
				empresas.put(nome, Objects.requireNonNull(Empresa.load(
						Objects.requireNonNull(empresasDoc.get(nome, Document.class))
				)));
			}
			return new Funcionario(empresas);
		} catch (NullPointerException | ClassCastException e) {
			return null;
		}
	}
	
	public static class Empresa {
		private final String[] beneficios;
		private final Document campos;
		
		private Empresa(String[] beneficios, Document campos) {
			this.beneficios = beneficios;
			this.campos = campos;
		}
		
		private static Empresa load(Document document) {
			try {
				return new Empresa(
						Objects.requireNonNull(document.getList(Field.beneficios, String.class))
								.toArray(String[]::new),
						Objects.requireNonNull(document.get(Field.campos, Document.class)));
			} catch (NullPointerException | ClassCastException e) {
				return null;
			}
		}
		
		public Document save() {
			return new Document()
					.append(Field.beneficios, Arrays.asList(this.beneficios))
					.append(Field.campos, campos);
		}
	}
}
