package localOps;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import data.Empresa;
import data.Field;
import org.bson.Document;
import utils.EnvUtils;

import java.util.LinkedList;

import static database.DatabaseCommons.*;

class DatabaseGenerator {
	private static MongoCollection<Document> empresas;
	private static MongoClient mongoClient;
	
	static void init() {
		mongoClient = MongoClients.create(EnvUtils.DB_connection_string);
		MongoDatabase database = mongoClient.getDatabase(EnvUtils.database_name);
		LinkedList<String> collectionNames = getAll(database.listCollectionNames());
		empresas = getCollection(database, collectionEmpresasName, collectionNames);
		//No need to store the 'funcionarios' collection since we aren't going to use it here.
		getCollection(database, collectionFuncionariosName, collectionNames);
	}
	
	private static MongoCollection<Document> getCollection(
			MongoDatabase database, String collectionName, LinkedList<String> collectionNames) {
		if (!collectionNames.contains(collectionName)) {
			getFirst(database.createCollection(collectionName));
		}
		return database.getCollection(collectionName);
	}
	
	static UpdateResult registerEmpresa(Empresa empresa){
		return getFirst(empresas.replaceOne(Filters.eq(Field.nome, empresa.nome),
				empresa.save(),
				new ReplaceOptions().upsert(true)));
	}
	
	static void close() {
		mongoClient.close();
	}
}
