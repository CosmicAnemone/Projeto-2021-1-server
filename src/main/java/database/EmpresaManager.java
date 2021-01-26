package database;

import ch.qos.logback.classic.Logger;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import data.Empresa;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.LoggerFactory;
import utils.ExceptionOps;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EmpresaManager implements Subscriber<ChangeStreamDocument<Document>> {
	private static final Logger LOG = (Logger) LoggerFactory.getLogger(EmpresaManager.class);
	
	private static final HashMap<String, Empresa> empresas = new HashMap<>();
	private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public static final EmpresaManager singleton = new EmpresaManager();
	
	private EmpresaManager(){}
	
	public static Empresa getEmpresa(String nome){
		lock.readLock().lock();
		try {
			return empresas.get(nome);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public static void registerEmpresa(Empresa empresa){
		lock.writeLock().lock();
		try {
			empresas.put(empresa.nome, empresa);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	@Override
	public void onSubscribe(Subscription s) {
		LOG.trace("Subscribed");
	}
	
	@Override
	public void onNext(ChangeStreamDocument<Document> doc) {
		Empresa empresa = Empresa.load(doc.getFullDocument());
		if (empresa == null){
			LOG.error("Invalid document loaded");
		}
		else{
			registerEmpresa(empresa);
		}
	}
	
	@Override
	public void onError(Throwable t) {
		LOG.error("Error:\n" + ExceptionOps.print(t));
	}
	
	@Override
	public void onComplete() {
		LOG.warn("Subscriber complete. Shouldn't happen out of DB shutdown.");
	}
}
