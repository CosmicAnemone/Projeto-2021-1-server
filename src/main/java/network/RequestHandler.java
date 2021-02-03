package network;

import ch.qos.logback.classic.Logger;
import data.Field;
import data.Funcionario;
import database.DatabaseManager;
import database.EmpresaManager;
import database.FuncionarioManager;
import org.bson.BsonInvalidOperationException;
import org.bson.Document;
import org.bson.json.JsonParseException;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.http.Request;
import ratpack.util.MultiValueMap;

import java.util.Objects;

public class RequestHandler {
	private static final Logger LOG = (Logger) LoggerFactory.getLogger(RequestHandler.class);
	
	public static void handleGet(Context context) {
		Request request = context.getRequest();
		MultiValueMap<String, String> queries = request.getQueryParams();
		if (Objects.equals(queries.get(Field.type), RequestType.info_empresa.name())) {
			try {
				String nome = Objects.requireNonNull(queries.get(Field.empresa),
						"Pedido GET sem valor para a query " + Field.empresa);
				context.render(Objects.requireNonNull(EmpresaManager.getEmpresa(nome),
						"Pedido de informações sobre empresa inexistente: " + nome)
						.saveBeneficios().toJson());
			} catch (NullPointerException e) {
				invalidRequest(context, e.getMessage());
			}
		} else {
			invalidRequest(context, "Pedido GET com valor inválido para a query " + Field.type);
		}
		
	}
	
	public static void handlePost(Context context) {
		Request request = context.getRequest();
		MultiValueMap<String, String> queries = request.getQueryParams();
		RequestType requestType;
		try {
			requestType = RequestType.valueOf(queries.get(Field.type));
		} catch (NullPointerException e) {
			invalidRequest(context, "Pedido POST sem valor para a query " + Field.type);
			return;
		} catch (IllegalArgumentException e) {
			invalidRequest(context, "Pedido POST com valor inválido para a query " + Field.type);
			return;
		}
		request.getBody().then(body -> {
			Document document;
			try {
				document = Document.parse(body.getText());
			} catch (JsonParseException | IllegalArgumentException | BsonInvalidOperationException e) {
				invalidRequest(context, "Pedido POST com JSON inválido");
				return;
			}
			try {
				long CPF = Objects.requireNonNull(document.getLong(Field.funcionario),
						"Pedido POST sem valor para o campo " + Field.funcionario);
				String nomeEmpresa = Objects.requireNonNull(document.getString(Field.empresa),
						"Pedido POST sem valor para o campo " + Field.empresa);
				switch (requestType) {
					case info_empresa -> invalidRequest(context,
							"Pedido de informação de empresa como POST. Deveria ser GET");
					case info_funcionario -> context
							.render(Funcionario.saveEmpresaOrEmpty(
									DatabaseManager.getFuncionario(CPF, nomeEmpresa),
									nomeEmpresa).toJson());
					case add -> {
						String beneficio = Objects.requireNonNull(document.getString(Field.beneficio),
								"Pedido POST para adicionar benefício sem valor para o campo " + Field.beneficio);
						Document campos = Objects.requireNonNull(document.get(Field.campos, Document.class),
								"Pedido POST para adicionar benefício sem valor para o campo " + Field.campos);
						String result = FuncionarioManager.addBeneficio(CPF, nomeEmpresa, beneficio, campos);
						if (result == null) {
							emptySuccess(context);
						} else {
							invalidRequest(context,
									"Erro no pedido POST para adicionar benefício. Causa:\n" + result);
						}
					}
					case edit -> {
						String beneficio = Objects.requireNonNull(document.getString(Field.beneficio),
								"Pedido POST para editar benefício sem valor para o campo " + Field.beneficio);
						Document campos = Objects.requireNonNull(document.get(Field.campos, Document.class),
								"Pedido POST para editar benefício sem valor para o campo " + Field.campos);
						String result = FuncionarioManager.editBeneficio(CPF, nomeEmpresa, beneficio, campos);
						if (result == null) {
							emptySuccess(context);
						} else {
							invalidRequest(context,
									"Erro no pedido POST para editar benefício. Causa:\n" + result);
						}
					}
					case remove -> {
						String beneficio = Objects.requireNonNull(document.getString(Field.beneficio),
								"Pedido POST para remover benefício sem valor para o campo " + Field.beneficio);
						String result = FuncionarioManager.removeBeneficio(CPF, nomeEmpresa, beneficio);
						if (result == null) {
							emptySuccess(context);
						} else {
							invalidRequest(context,
									"Erro no pedido POST para remover benefício. Causa:\n" + result);
						}
					}
				}
			} catch (NullPointerException e) {
				invalidRequest(context, e.getMessage());
			} catch (ClassCastException e) {
				// Isso **realmente** não deveria acontecer.
				// Alguém teria que registrar um nome de empresa como número ou data.
				// Daí o log mais relaxado. O caso específico deveria ser óbvio a partir daí.
				invalidRequest(context,
						"Pedido POST com tipo inválido para o campo " + Field.empresa + " ou " + Field.beneficio);
			}
		});
	}
	
	private static void emptySuccess(Context context) {
		context.render(new Document(Field.type, "success").toJson());
	}
	
	private static void invalidRequest(Context context, String logMessage) {
		LOG.warn(logMessage);
		context.render(new Document(Field.type, "error").append(Field.data, "Invalid Request").toJson());
	}
	
	public enum RequestType {
		info_empresa, info_funcionario, add, edit, remove
	}
}
