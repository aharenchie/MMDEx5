import java.applet.*;
import java.awt.*;
import java.awt.image.*;

public class grabTest extends Applet implements Runnable{

	Image img;
	Image new_img;
	
	int w = 0; //元になるイメージの横幅を代入する
	int h = 0; //元になるイメージの縦幅を代入する
	int x,y; //上下反転するエリアの位置を格納する変数
	
	int pix[]; //元になるイメージを格納するための配列
	int tmp_pix[];
	int new_pix[]; //変更後のイメージを格納するための配列
	int test_pix[];
	
	Thread t;
	
	public void init(){
		
		img = getImage(getCodeBase(),"sample0-15.jpg");
		
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(img,0);
		
		try{
			mt.waitForID(0);
		} catch (InterruptedException e){}

		w = img.getWidth(this); //ロードしたイメージの横幅を取得
		h = img.getHeight(this); //ロードしたイメージの縦幅を取得
		x = 0;
		y = 0;
		
		pix = new int[w*h];
		tmp_pix = new int[(w/4)*(h/4)];
		test_pix = new int[(w/4)*(h/4)];
		new_pix = new int[w*h];
		
		
		t = new Thread(this);
		t.start(); //スレッドスタート
	}
	
	public void paint(Graphics g){
		MemoryImageSource mimg = new MemoryImageSource(w,h,new_pix,0,w);
		new_img = createImage(mimg);
		g.drawImage(img, 0, 0, this);
		g.drawImage(new_img, w+5, 0, this);
	
	}
	
	public void run() {
		//無限ループ
		while(true){
			
			try{		
				
				PixelGrabber pg = new PixelGrabber(img,0,0,w,h,pix,0,w);
				pg.grabPixels();		 
	
					flipVertical(x,y,pix,new_pix);//上下反転
					repaint(); //再描画
					x++;//横方向に移動
					Thread.sleep(1000);//5秒スリープ
					
					//横方向の末尾まで移動 and 縦方向の末尾ではない 時
					if(x == 4 && y != 4){
						y++;//縦方向に移動
						x = 0;//横方向の先頭に移動
					}
					//最初に戻る
					if(y == 4){
						y = 0;x = 0;//縦方向、横方向の先頭に移動
					}
						
			}catch (InterruptedException e){}
		}
	}

	public int[] flipVertical(int x,int y,int pix[],int new_pix[]){
		
			int tmp_x = w*1/4*x; 
			int tmp_y = h*1/4*y;
			int new_p = 0;
			
			/*基本*/
			for(int i=0;i < w*h;i++)
				new_pix[i] = pix[i];
				
		
			//上下反転(初期位置:上下反転するエリアの位置)
			for(int i=tmp_y;i < tmp_y+h/4; i++){
				for(int j=tmp_x;j < tmp_x+w/4;j++){
					int p = w*i+j;
					//new_pix[p] = pix[p];
					tmp_pix[new_p] = pix[p];
					new_p++;
					//new_pix[p] = pix[w*h-p-1]; 
				}			
			}
			
		
		//上下反転
			for(int i=0;i < h/4; i++){
				for(int j=0;j < w/4;j++){
					test_pix[w/4*i+j] = tmp_pix[(w/4)*(((h/4)-1)-i)+j];
				}
			}
			
			new_p = 0;
			for(int i=tmp_y;i < tmp_y+h/4; i++){
				for(int j=tmp_x;j < tmp_x+w/4;j++){
					int p = w*i+j;
					new_pix[p] =test_pix[new_p];
					new_p++;
					//new_pix[p] = pix[w*h-p-1]; 
				}			
			}
			
			
		return new_pix;
	}

}
