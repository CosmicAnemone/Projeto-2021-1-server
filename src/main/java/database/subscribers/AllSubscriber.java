package database.subscribers;

import org.reactivestreams.Publisher;

import java.util.LinkedList;

public class AllSubscriber<T> extends BlockingSubscriber<T> {
	private final LinkedList<T> resp = new LinkedList<>();

	public AllSubscriber(Publisher<T> publisher) {
		super();
		publisher.subscribe(this);
	}
	
	public LinkedList<T> get() {
		await();
		return resp;
	}

	@Override
	public void onNext(final T d) {
		synchronized(resp){
			resp.add(d);
		}
	}

	@Override
	public void onError(final Throwable t) {
		t.printStackTrace();
		onComplete();
	}

	@Override
	public void _onComplete() { }
}
