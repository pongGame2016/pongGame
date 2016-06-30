package player2;

import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import com.tibco.tibrv.*;

public class PanelPelota extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	// Positions on X and Y for the ball, player 1 and player 2
	private int pelotaX = 10, pelotaY = 100, jug1X = 10, jug1Y = 100, jug2X = 230, jug2Y = 100;
	Thread hilo;
	int derecha = 5; // to the right
	int izquierda = -5; // to the left
	int arriba = 5; // upward
	int abajo = -5; // down
	int ancho, alto; // Width and height of the ball
	// Scores
	int maxScore = 109000000;
	int contPlay1 = 0, contPlay2 = 0;
	boolean player1FlagArr, player1FlagAba, player2FlagArr, player2FlagAba;
	boolean juego, gameOver;

	String service = "6500";
	String network = ";224.9.9.10";
	String daemon = null;
	TibrvTransport transport = null;
	TibrvDispatcher tibrvDispatcher;

	private TibrvListener tibrvListener;

	public PanelPelota() {
		juego = true;
		hilo = new Thread(this);
		hilo.start();

		try {
			Tibrv.open(Tibrv.IMPL_NATIVE);

			transport = new TibrvRvdTransport(service, network, daemon);

			tibrvListener = new TibrvListener(Tibrv.defaultQueue(), new MyTibCallback(), transport, "PLAYER2", null);
			tibrvDispatcher = new TibrvDispatcher("DefaultQueue", Tibrv.defaultQueue());
		} catch (TibrvException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Draw ball and ships
	public void paintComponent(Graphics gc) {
		setOpaque(false);
		super.paintComponent(gc);

		// Draw ball
		gc.setColor(Color.black);
		gc.fillOval(pelotaX, pelotaY, 8, 8);

		// Draw ships
		gc.fillRect(jug1X, jug1Y, 10, 25);
		gc.fillRect(jug2X, jug2Y, 10, 25);

		// Draw scores
		gc.drawString("Jugador1: " + contPlay1, 25, 10);
		gc.drawString("Jugador2: " + contPlay2, 150, 10);

		if (gameOver)
			gc.drawString("Game Over", 100, 125);
	}

	// Positions on X and Y for the ball
	public void dibujarPelota(int nx, int ny) {
		pelotaX = nx;
		pelotaY = ny;
		this.ancho = this.getWidth();
		this.alto = this.getHeight();
		repaint();
	}

	// Here we receive from the game container class the key pressed
	public void keyPressed(KeyEvent evt, TibrvMsg msg) {
		switch (evt.getKeyCode()) {
		// Move ship 1

		case KeyEvent.VK_UP:
			player2FlagArr = true;
			// send msg if player2FlagAba=true
			sendPlayer1UP();
			break;
		case KeyEvent.VK_DOWN:
			player2FlagAba = true;
			// send msg if player2FlagAba=true
			sendPlayer1DOWN();
			break;
		}
	}

	private void sendPlayer1UP() {
		TibrvMsg msg = new TibrvMsg();
		try {
			msg.setSendSubject("PLAYER1");

			msg.update("up", true);

			// .....

			transport.send(msg);
		} catch (TibrvException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendPlayer1DOWN() {
		TibrvMsg msg = new TibrvMsg();
		try {
			msg.setSendSubject("PLAYER1");

			msg.update("up", false);

			// .....

			transport.send(msg);
		} catch (TibrvException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendPlayer1STOP() {
		TibrvMsg msg = new TibrvMsg();
		try {
			msg.setSendSubject("PLAYER1");

			msg.update("stop", true);
			// .....

			transport.send(msg);
		} catch (TibrvException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Here we receive from the game container class the key released
	public void keyReleased(KeyEvent evt, TibrvMsg msg) {
		switch (evt.getKeyCode()) {
		// Mover Nave1
		case KeyEvent.VK_UP:
			player2FlagArr = false;
			// send msg if player1FlagArr=false
			sendPlayer1STOP();
			break;
		case KeyEvent.VK_DOWN:
			player2FlagAba = false;
			// send msg if player1FlagAba=false
			sendPlayer1STOP();
			break;

		}
	}

	// Move player 1
	public void moverPlayer1() {
		if (player1FlagArr == true && jug1Y >= 0)
			jug1Y += abajo;
		if (player1FlagAba == true && jug1Y <= (this.getHeight() - 25))
			jug1Y += arriba;
		dibujarPlayer1(jug1X, jug1Y);
	}

	// Move player 2
	public void moverPlayer2() {
		if (player2FlagArr == true && jug2Y >= 0)
			jug2Y += abajo;
		if (player2FlagAba == true && jug2Y <= (this.getHeight() - 25))
			jug2Y += arriba;
		dibujarPlayer2(jug2X, jug2Y);
	}

	// Position on Y for the player 1
	public void dibujarPlayer1(int x, int y) {
		this.jug1X = x;
		this.jug1Y = y;
		repaint();
	}

	// Position on Y for the player 2
	public void dibujarPlayer2(int x, int y) {
		this.jug2X = x;
		this.jug2Y = y;
		repaint();
	}

	public void run() {
		// TODO Auto-generated method stub
		boolean izqDer = false;
		boolean arrAba = false;

		while (true) {

			if (juego) {

				// The ball move from left to right
				if (izqDer) {
					// a la derecha
					pelotaX += derecha;
					if (pelotaX >= (ancho - 8))
						izqDer = false;
				} else {
					// a la izquierda
					pelotaX += izquierda;
					if (pelotaX <= 0)
						izqDer = true;
				}

				// The ball moves from up to down
				if (arrAba) {
					// hacia arriba
					pelotaY += arriba;
					if (pelotaY >= (alto - 8))
						arrAba = false;

				} else {
					// hacia abajo
					pelotaY += abajo;
					if (pelotaY <= 0)
						arrAba = true;
				}
				dibujarPelota(pelotaX, pelotaY);

				// Delay
				try {
					Thread.sleep(50);
				} catch (InterruptedException ex) {

				}

				// Move player 1
				moverPlayer1();

				// Move player 2
				moverPlayer2();

				// The score of the player 1 increase
				if (pelotaX >= (ancho - 8))
					contPlay1++;

				// The score of the player 2 increase
				if (pelotaX == 0)
					contPlay2++;

				// Game over. Here you can change 6 to any value
				// When the score reach to the value, the game will end
				if (contPlay1 == maxScore || contPlay2 == maxScore) {
					juego = false;
					gameOver = true;
				}

				// The ball stroke with the player 1
				if (pelotaX == jug1X + 10 && pelotaY >= jug1Y && pelotaY <= (jug1Y + 25))
					izqDer = true;

				// The ball stroke with the player 2
				if (pelotaX == (jug2X - 5) && pelotaY >= jug2Y && pelotaY <= (jug2Y + 25))
					izqDer = false;
			}

		}

	}

	private class MyTibCallback implements TibrvMsgCallback {

		@Override
		public void onMsg(TibrvListener arg0, TibrvMsg msg) {

			try {
				if (msg.get("up") != null) {
					if ((boolean) msg.get("up")) {
						player1FlagArr = true;
					} else {
						player1FlagAba = true;
					}
				}
				// System.out.println(msg.get("KeyEvent.VK_W"));
			} catch (TibrvException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (msg.get("stop") != null) {
					if ((boolean) msg.get("stop")) {
						player1FlagArr = false;
						player1FlagAba = false;
					}
				}
				// System.out.println(msg.get("KeyEvent.VK_W"));
			} catch (TibrvException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
