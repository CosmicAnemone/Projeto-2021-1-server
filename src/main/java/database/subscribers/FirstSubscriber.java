package database.subscribers;

import org.reactivestreams.Publisher;

public class FirstSubscriber<T> extends BlockingSubscriber<T> {
	private T resp = null;

	public FirstSubscriber(Publisher<T> publisher) {
		super();
		publisher.subscribe(this);
	}
	
	public T get() {
		await();
		return resp;
	}

	@Override
	public void onNext(final T d) {
		resp = d;
		onComplete();
	}

	@Override
	public void onError(final Throwable t) {
		t.printStackTrace();
		onComplete();
	}

	@Override
	public void _onComplete() { }
}
