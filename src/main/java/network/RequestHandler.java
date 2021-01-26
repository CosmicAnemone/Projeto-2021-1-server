package network;

import data.Field;
import data.Funcionario;
import database.DatabaseManager;
import database.EmpresaManager;
import database.FuncionarioManager;
import org.bson.Document;
import org.bson.json.JsonParseException;
import ratpack.handling.Context;
import ratpack.http.Request;
import ratpack.util.MultiValueMap;

import java.util.Objects;

public class RequestHandler {
	public static void handleGet(Context context) {
		Request request = context.getRequest();
		MultiValueMap<String, String> queries = request.getQueryParams();
		if (Objects.equals(queries.get(Field.type), RequestType.info_empresa.name())) {
			try {
				String nome = Objects.requireNonNull(queries.get(Field.empresa));
				context.render(Objects.requireNonNull(EmpresaManager.getEmpresa(nome)).saveBeneficios().toJson());
			} catch (NullPointerException e) {
				invalidRequest(context);
			}
		} else {
			invalidRequest(context);
		}
		
	}
	
	public static void handlePost(Context context) {
		Request request = context.getRequest();
		MultiValueMap<String, String> queries = request.getQueryParams();
		RequestType requestType;
		try {
			requestType = RequestType.valueOf(queries.get(Field.type));
		} catch (IllegalArgumentException e) {
			invalidRequest(context);
			return;
		}
		request.getBody().then(body -> {
			Document document;
			try {
				document = Document.parse(body.getText());
			} catch (JsonParseException | IllegalArgumentException e) {
				invalidRequest(context);
				return;
			}
			try {
				long CPF = Objects.requireNonNull(document.getLong(Field.funcionario));
				String nomeEmpresa = Objects.requireNonNull(document.getString(Field.empresa));
				switch (requestType) {
					case info_empresa -> invalidRequest(context);
					case info_funcionario -> {
						Funcionario funcionario = Objects.requireNonNull(DatabaseManager.getFuncionario(
								CPF,
								nomeEmpresa
						));
						context.render(Objects.requireNonNull(funcionario.empresas.get(nomeEmpresa))
								.save().toJson());
					}
					case add -> {
						String beneficio = Objects.requireNonNull(document.getString(Field.beneficio));
						Document campos = Objects.requireNonNull(document.get(Field.campos, Document.class));
						if (FuncionarioManager.addBeneficio(CPF, nomeEmpresa, beneficio, campos)) {
							emptySuccess(context);
						} else {
							invalidRequest(context);
						}
					}
					case edit -> {
						String beneficio = Objects.requireNonNull(document.getString(Field.beneficio));
						Document campos = Objects.requireNonNull(document.get(Field.campos, Document.class));
						if (FuncionarioManager.editBeneficio(CPF, nomeEmpresa, beneficio, campos)) {
							emptySuccess(context);
						} else {
							invalidRequest(context);
						}
					}
					case remove -> {
						String beneficio = Objects.requireNonNull(document.getString(Field.beneficio));
						if (FuncionarioManager.removeBeneficio(CPF, nomeEmpresa, beneficio)) {
							emptySuccess(context);
						} else {
							invalidRequest(context);
						}
					}
				}
			} catch (ClassCastException | NullPointerException e) {
				invalidRequest(context);
			}
		});
	}
	
	private static void emptySuccess(Context context) {
		context.render(new Document(Field.type, "success").toJson());
	}
	
	private static void invalidRequest(Context context) {
		context.render(new Document(Field.type, "error").append(Field.data, "Invalid Request").toJson());
	}
	
	public enum RequestType {
		info_empresa, info_funcionario, add, edit, remove
	}
}
