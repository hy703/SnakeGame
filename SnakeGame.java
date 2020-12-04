package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedList;
import java.util.Random;


public class SnakeGame {
	static class MyFrame extends JFrame {		
		//x위치와 y위치를 가지고 있는 클래스
		static class XY{
			int x;
			int y;
			public XY(int x, int y) {
				this.x = x;
				this.y = y;
			}
		}
		static JPanel panelNorth; //북쪽 영역 패널 선언
		static JPanel panelCenter; //가운데 들어가는 바둑판모양의 판
		static JLabel labelTitle; //전체적인 title영역
		static JLabel labelMessage; //메세지 영역
		static JPanel[][] panels = new JPanel[20][20]; //뱀이 돌아다니는 바둑판 (2차배열)
		static int[][] map = new int[20][20]; //실제적인 데이터를 가지고있는 int Array (2차배열)  Fruit9, Bomb8, 0 Blank
		static LinkedList<XY> snake = new LinkedList<XY>(); //뱀 몸뚱아리, 뱀의 몸이 계속 늘어날때 하나씩 증가
		static int dir = 3; //진행 방향 - 0: 위로, 1: 아래로, 2: 왼쪽, 3: 오른쪽
		static int score = 0; //과일을 먹을때마다 점수를 나타내는 점수판
		static int time = 0; // 게임시간을 표시 - 1초단위로 움직인다.
		static int timeTickCount = 0; //타이머 마다 움직임 - 200ms 단위로 움직인다.
		static Timer timer = null;
		
		
		
		public MyFrame(String title) {
			super(title);
			this.setSize(400, 500); //this 는 JFrame을 의미 (너비 400 높이 500)
			this.setVisible(true); //setVisible을 해줘야 ui가 보이게된다
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closeOperation을 지정해주어야 창이 닫히게 된다.
			
			initUI(); //전체적인 ui를 초기화하는 함수
			makeSnakeList(); // Snake Body를 만들어서 LinkedList에 넣는다
			startTimer(); //시작 타이머
			setKeyListener(); //키보드 이벤트
			makeFruit(); //과일 만드는 함수
		}
		public void makeFruit() {
			Random rand = new Random();
			//0~19 x, 0~19 y
			int randX = rand.nextInt(19);
			int randY = rand.nextInt(19);
			
			map[randX][randY] = 9; // 9:Fruit
		}
		public void setKeyListener() {
			this.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_UP) {
						if (dir != 1) {
							dir = 0;
						}
					} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
						if (dir != 0) {
							dir = 1;
						}
					} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						if (dir != 3) {
							dir = 2;
						}
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						if (dir != 2) {
							dir = 3;
						}
					}
				}
			});
		}
		public void startTimer() {
			timer = new Timer(5 , new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					timeTickCount += 1;
					
					if(timeTickCount % 20 == 0) {
						time ++; //1초가 증가
						
						moveSnake(); //뱀 움직임 갱신
						updateUI(); //ui갱신
					}
				}
			});
			timer.start(); //게임시작!
		}
		//뱀 움직임
		public void moveSnake() {
			XY headXY = snake.get(0); //뱀의 머리
			int headX = headXY.x;
			int headY = headXY.y;
			
			if(dir == 0) { // 진행방향 - 0:위로, 1:아래로, 2:왼쪽, 3:오른쪽
				boolean isColl = checkCollision(headX, headY-1);
				if(isColl == true) { //게임끝
					labelMessage.setText("Game Over!");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX, headY-1)); //머리가 위로 이동
				snake.remove(snake.size()-1); // 꼬리 위치 삭제
			}else if(dir == 1){
				boolean isColl = checkCollision(headX, headY+1);
				if(isColl == true) { //게임끝
					labelMessage.setText("Game Over!");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX, headY+1)); //머리가 위로 이동
				snake.remove(snake.size()-1); // 꼬리 위치 삭제
			}else if(dir == 2) {
				boolean isColl = checkCollision(headX-1, headY);
				if(isColl == true) { //게임끝
					labelMessage.setText("Game Over!");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX-1, headY)); //머리가 위로 이동
				snake.remove(snake.size()-1); // 꼬리 위치 삭제
			}else if(dir == 3) {
				boolean isColl = checkCollision(headX+1, headY);
				if(isColl == true) { //게임끝
					labelMessage.setText("Game Over!");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX+1, headY)); //머리가 위로 이동
				snake.remove(snake.size()-1); // 꼬리 위치 삭제
			}
		}
		public boolean checkCollision(int headX, int headY) {
			if(headX<0||headX>19||headY<0||headY>19) { //머리가 벽에 충돌한 상황
				return true;
			}
			//뱀 몸통에 충돌할때
			for(XY xy : snake) {
				if(headX == xy.x && headY == xy.y) {
					return true;
				}
			}
			if(map[headY][headX]==9) { //Fruit 에 뱀이 충돌
				map[headY][headX] = 0;
				addTail();
				makeFruit();
				score += 100;
			}
			return false;
		}
		public void addTail() {
			int tailX = snake.get(snake.size()-1).x;
			int tailY = snake.get(snake.size()-1).y;
			int tailX2 = snake.get(snake.size()-2).y;
			int tailY2 = snake.get(snake.size()-2).y;
			
			if(tailX<tailX2) { //진행방향이 오른쪽
				snake.add(new XY(tailX-1, tailY));
			}else if(tailX>tailX2) { //진행방향이 왼쪽
				snake.add(new XY(tailX+1, tailY));
			}else if(tailY<tailY2) { //진행방향이 위쪽
				snake.add(new XY(tailX, tailY-1));
			}else if(tailY>tailY2) { //진행방향이 아래쪽
				snake.add(new XY(tailX, tailY+1));
			}
		}
		//ui 갱신
		public void updateUI() {
			labelTitle.setText("Score: " + score + "Time: " + time);
			
			//crear tile(panel) 전체 ui 삭제
			for(int i=0; i<20 ; i++) {
				for(int j=0; j<20 ; j++) {
					if(map[i][j] == 0) { // 0이면 빈공간
						panels[i][j].setBackground(Color.GRAY);
					}
					else if(map[i][j] == 9) { //Fruit
						panels[i][j].setBackground(Color.GREEN);
					}
				}
			}
			
			//뱀 그리기
			int index = 0;
			for( XY xy: snake) {
				if(index == 0) { // 뱀의 머리
					panels[xy.y][xy.x].setBackground(Color.RED);
				}else { // 뱀의 몸(하나일 수도 있고 여러개일수도 있다) , 뱀의 꼬리
					panels[xy.y][xy.x].setBackground(Color.BLUE);
				}
				index++;
			}
		}
		public void makeSnakeList() {
			snake.add(new XY(10,10)); //뱀의 머리
			snake.add(new XY(9,10));//뱀의 몸통
			snake.add(new XY(8,10)); //뱀의 꼬리
		}
		public void initUI() {
			this.setLayout(new BorderLayout());
			
			//위쪽에 검은색 영역
			panelNorth = new JPanel(); //북쪽 패널
			panelNorth.setPreferredSize(new Dimension(400, 100)); //사이즈 지정 (400, 100)정도만 되면 위쪽에 메세지 영역을 충분히 표현할 수 있다.
			panelNorth.setBackground(Color.BLACK); //배경 색깔을 검은색으로 지정
			panelNorth.setLayout(new FlowLayout()); //단순하게 비치되는 순서대로 가운데에 자유롭게 배치하는 구조가 FlowLayout
			
			//흰색글씨의 제목영역
			labelTitle = new JLabel("Score: 0, Time: 0Sec"); //레이블 타이틀의 초기화 값 지정
			labelTitle.setPreferredSize(new Dimension(400,50)); //사이즈 지정
			labelTitle.setFont(new Font("TimesNewNorman",Font.BOLD,20)); //기본 폰트로 지정 (굵게, 사이즈20)
			labelTitle.setForeground(Color.WHITE); //글씨색깔 흰색으로 지정
			labelTitle.setHorizontalAlignment(JLabel.CENTER); //글씨위치 가운데로 지정
			panelNorth.add(labelTitle); //panelNorth에 위에설정대로 추가
			
			//노란색 글씨의 메세지영역
			labelMessage = new JLabel("Eat Fruit!"); //명령어 지정
			labelMessage.setPreferredSize(new Dimension(400,20)); //사이즈 지정
			labelMessage.setFont(new Font("TimesNewNorman",Font.BOLD,20)); //기본 폰트로 지정 (굵게, 사이즈20)
			labelMessage.setForeground(Color.YELLOW); //글씨색깔 노란색으로 지정
			labelMessage.setHorizontalAlignment(JLabel.CENTER); //글씨위치 가운데로 지정
			panelNorth.add(labelMessage); //panelNorth에 위에설정대로 추가
			
			this.add("North",panelNorth); //panelNorth를 MyFrame 북쪽에 추가
			
			//실제 게임판의 회색영역
			panelCenter = new JPanel();
			panelCenter.setLayout(new GridLayout(20,20)); //panelCenter의 사이즈를 지정 - 바둑판 모양이기 때문에 GridLayout을 사용하면 편하다 - 셀의 개수(20,20)
			//
			for(int i=0; i<20 ; i++) { //i 루프 : 열
				for(int j=0; j<20; j++) {// j 루프 : 행
					map[i][j] = 0; //init 0 : 빈공간
					panels[i][j] = new JPanel(); //실제 패널이 여기서 만들어 진다.
					panels[i][j].setPreferredSize(new Dimension(20,20)); //사이즈 지정
					panels[i][j].setBackground(Color.GRAY); // 배경 색깔을 회색으로 지정
					panelCenter.add(panels[i][j]); //패널을 패널센터에 하나씩 추가해준다.
					
				}
			}
			this.add("Center",panelCenter); //panelCenter를 MyFrame 센터에 추가
			this.pack(); //빈 공간 삭제
		}
	}
	public static void main(String[] args) {
		new MyFrame("Snake Game");

	}

}
