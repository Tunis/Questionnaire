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

		try {
			Thread.sleep(22000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("fin des test");
		System.exit(0);
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
		// ici un pool est créé a chaque appel de la methode. (et donc autant de thread que le pool a a chaque appel)
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(TestTimer::stopExcecutor, 5, SECONDS);
		// appel d'une methode sans parametre on peut utilisé la methode reference a la place d'une lambda.
	}

	private static void scheduleExecutorGlobal(int secondes) {
		// ici un pool commun a chaque appel de la methode. (dans ce cas, les 4 timer sont sur le meme thread sans probleme)

		schedulerGlobal.schedule(() -> stopExcecutorGlobal(secondes), secondes, SECONDS);
		// si on passe un parametre a la methode appelé on doit utiliser la version lambda standard.
	}

	private static void timerTask() {
		// class Timer est ancienne a ne plus utiliser au profit des Executors
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
