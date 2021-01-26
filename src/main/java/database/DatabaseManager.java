package database;

import ch.qos.logback.classic.Logger;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import data.Empresa;
import data.Field;
import data.Funcionario;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.LoggerFactory;
import utils.EnvUtils;

import java.util.ArrayList;
import java.util.Objects;

import static database.DatabaseCommons.*;

public class DatabaseManager {
	private static final Logger LOG = (Logger) LoggerFactory.getLogger(DatabaseManager.class);
	
	private static MongoClient mongoClient;
	private static MongoCollection<Document> funcionarios;
	
	public static void init() {
		mongoClient = MongoClients.create(EnvUtils.DB_connection_string);
		MongoDatabase database = mongoClient.getDatabase(EnvUtils.database_name);
		MongoCollection<Document> empresas = database.getCollection(collectionEmpresasName);
		empresas.watch().subscribe(EmpresaManager.singleton);
		getAll(empresas.find()).forEach(doc -> {
			Empresa empresa = Empresa.load(doc);
			if (empresa == null) {
				LOG.error("Invalid document loaded");
			} else {
				EmpresaManager.registerEmpresa(empresa);
			}
		});
		funcionarios = database.getCollection(collectionFuncionariosName);
	}
	
	public static Funcionario getFuncionario(long CPF, String empresa) {
		try {
			return Funcionario.load(
					Objects.requireNonNull(getFirst(funcionarios.find(
							Filters.and(
									Filters.eq(Field.CPF, CPF),
									Filters.exists(Field.empresas + "." + empresa)
							)
					)))
			);
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public static boolean addBeneficio(long CPF, String empresa, String beneficio, ArrayList<Bson> updates){
		updates.add(Updates.setOnInsert(Field.CPF, CPF));
		updates.add(Updates.addToSet(Field.empresas + "." + empresa + "." + Field.beneficios, beneficio));
		return getFirst(funcionarios.updateOne(
				Filters.eq(Field.CPF, CPF),
				Updates.combine(updates),
				new UpdateOptions().upsert(true)
		)).wasAcknowledged();
	}
	
	public static boolean editBeneficio(long CPF, String empresa, String beneficio, ArrayList<Bson> updates){
		return getFirst(funcionarios.updateOne(
				Filters.and(
						Filters.eq(Field.CPF, CPF),
						Filters.eq(Field.empresas + "." + empresa + "." + Field.beneficios, beneficio)
				),
				Updates.combine(updates)
		)).wasAcknowledged();
	}
	
	public static boolean removeBeneficio(long CPF, String empresa, String beneficio) {
		return getFirst(funcionarios.updateOne(
				Filters.eq(Field.CPF, CPF),
				Updates.pull(Field.empresas + "." + empresa + "." + Field.beneficios, beneficio)
		)).wasAcknowledged();
	}
	
	public static void close() {
		mongoClient.close();
	}
}
