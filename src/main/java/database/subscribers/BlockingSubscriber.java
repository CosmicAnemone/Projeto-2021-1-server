package database.subscribers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.mongodb.MongoTimeoutException;

public abstract class BlockingSubscriber<T> implements Subscriber<T> {
	private final CountDownLatch latch;

	public BlockingSubscriber() {
		this.latch = new CountDownLatch(1);
	}

	@Override
	public final void onSubscribe(final Subscription s) {
		s.request(Long.MAX_VALUE);
		await();
	}
	
	public void await() {
		try {
			if (!latch.await(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
				throw new MongoTimeoutException("Publisher onComplete timed out");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(final Throwable t) {
		t.printStackTrace();
		onComplete();
	}

	@Override
	public final void onComplete() {
		_onComplete();
		latch.countDown();
	}
	
	public abstract void _onComplete();
}
