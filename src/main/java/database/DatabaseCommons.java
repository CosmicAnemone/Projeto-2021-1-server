package database;

import database.subscribers.AllSubscriber;
import database.subscribers.FirstSubscriber;
import org.reactivestreams.Publisher;

import java.util.LinkedList;

public class DatabaseCommons {
	public static final String collectionEmpresasName = "empresas";
	public static final String collectionFuncionariosName = "funcionarios";
	
	public static <T> T getFirst(Publisher<T> publisher) {
		return new FirstSubscriber<>(publisher).get();
	}
	
	public static <T> LinkedList<T> getAll(Publisher<T> publisher) {
		return new AllSubscriber<>(publisher).get();
	}
}
