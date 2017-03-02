import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TestTimer {
	private static ScheduledExecutorService schedulerGlobal = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {
		// scheduleExecutorService est la version a jour des Timer.

		System.out.println("lancement test :");
		timerTask();
		scheduleExecutor();
		scheduleExecutor();
		scheduleExecutor();
		scheduleExecutor();
		scheduleExecutorGlobal(5);
		scheduleExecutorGlobal(10);
		scheduleExecutorGlobal(15);
		scheduleExecutorGlobal(20);


		System.out.println();
	}


	private static void stopTimer() {
		System.out.println("stop timer");
	}

	private static void stopExcecutor() {
		System.out.println("stop excecutor new pool");
	}

	private static void stopExcecutorGlobal(int secondes) {
		System.out.println("stop excecutor de " + secondes);
	}


	private static void scheduleExecutor() {
		// scheduledExecutorService est un system de pool thread pour les tache planifié, le nombre passé en parametre correspond au nombre de thread du pool dans le cas d'un tache effectué plusieur fois dans le meme pool.
		// ici un pool a chaque appel de la methode.
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(TestTimer::stopExcecutor, 5, SECONDS);
		//System.exit(0);
	}

	private static void scheduleExecutorGlobal(int secondes) {
		// scheduledExecutorService est un system de pool thread pour les tache planifié, le nombre passé en parametre correspond au nombre de thread du pool dans le cas d'un tache effectué plusieur fois dans le meme pool.
		// ici un pool a chaque appel de la methode.

		schedulerGlobal.schedule(() -> stopExcecutorGlobal(secondes), secondes, SECONDS);
		//System.exit(0);
	}

	private static void timerTask() {
		Timer durationActuel = new Timer();
		int durationMax = 5000;

		durationActuel.schedule(new TimerTask() {
			@Override
			public void run() {
				stopTimer();
			}
		}, durationMax);
	}
}
