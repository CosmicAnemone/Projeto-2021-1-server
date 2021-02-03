package data;

import org.bson.Document;

import java.util.HashMap;
import java.util.Objects;

public class Empresa {
	public final String nome;
	public final HashMap<String, Beneficio> beneficios;
	
	public Empresa(String nome, ModeloBeneficio... modelos) {
		this.nome = nome;
		beneficios = new HashMap<>();
		for(ModeloBeneficio modelo:modelos){
			beneficios.put(modelo.nome, modelo.beneficio);
		}
	}
	
	private Empresa(String nome, HashMap<String, Beneficio> beneficios) {
		this.nome = nome;
		this.beneficios = beneficios;
	}
	
	public static Empresa load(Document document) {
		try {
			Document beneficiosDoc = Objects.requireNonNull(document.get(Field.beneficios, Document.class));
			HashMap<String, Beneficio> beneficios = new HashMap<>();
			for (String nome : beneficiosDoc.keySet()) {
				beneficios.put(nome, Objects.requireNonNull(Beneficio.load(
						Objects.requireNonNull(beneficiosDoc.get(nome, Document.class))
				)));
			}
			return new Empresa(Objects.requireNonNull(document.getString(Field.nome)), beneficios);
		} catch (NullPointerException | ClassCastException e) {
			return null;
		}
	}
	
	public Document saveBeneficios(){
		Document document = new Document();
		beneficios.forEach((nome, beneficio) -> document.append(nome, beneficio.save()));
		return document;
	}
	
	public Document save() {
		return new Document(Field.nome, nome)
				.append(Field.beneficios, saveBeneficios());
	}
	
	public static class Beneficio {
		public final HashMap<String, String> campos;
		
		private Beneficio(HashMap<String, String> campos) {
			this.campos = campos;
			campos.put("CPF", Field.campo_inteiro);
		}
		
		private static Beneficio load(Document camposDoc) {
			HashMap<String, String> campos = new HashMap<>();
			try {
				for (String nomeCampo : camposDoc.keySet()) {
					campos.put(nomeCampo, Objects.requireNonNull(camposDoc.getString(nomeCampo)));
				}
			} catch (NullPointerException | ClassCastException e) {
				return null;
			}
			return new Beneficio(campos);
		}
		
		private Document save() {
			Document document = new Document();
			campos.forEach(document::append);
			document.remove(Field.CPF);
			return document;
		}
	}
	
	public static class ModeloBeneficio {
		private final String nome;
		private final Beneficio beneficio;
		
		public ModeloBeneficio(String nome) {
			this.nome = nome;
			beneficio = new Beneficio(new HashMap<>());
		}
		
		public ModeloBeneficio setCampo(String nome, String tipo){
			if (!nome.equals("CPF"))
				beneficio.campos.put(nome, tipo);
			return this;
		}
	}
}
