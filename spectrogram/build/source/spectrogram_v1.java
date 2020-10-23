import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class spectrogram_v1 extends PApplet {



// for live input
AudioIn in;
FFT fft;
int bands = 256;
float[] spectrum = new float[bands];

// for lil' chart
int margin = round(1080*0.05f);
int innermargin = round(1080*0.05f);
int innermargin_v = 0;

// for the big chart
int rowheight = (1080-margin*2)/3;
float cursorx = margin+innermargin;
int cursory = margin;

public void setup() {

  

  // live input from system mic
  in = new AudioIn(this, 0);
  in.start();

  // analysis
  fft = new FFT(this, bands);
  fft.input(in);

  cleanslate();

}

// press SPACE to clear screen
public void keyPressed(){
  if (key==' '){
    cleanslate();
  }
}
public void cleanslate() {
  background(255);
  fill(240);
  noStroke();
  rect(margin, margin, width-margin*2, height-margin*2);
  noFill();
  cursorx = margin+innermargin;
  cursory = margin;
}


public void lilchart() {

  // put some air between lines
  int everyN = 5;

  // select just a portion of the spectrum to draw, for compositional balance
  int start = 0;
  int end = round(bands*0.2f);

  // dimensions
  float h = 400;
  float w = (end-start)*everyN;
  float x = round((width - w) / 2);
  float y = height - margin - innermargin;

  // clear background
  noStroke();
  fill(240);
  rect(x,y-h/3,w,h/3);
  noFill();

  //draw individual lines
  stroke(125);
  for(int i = start; i < end; i++) {
    strokeWeight(1);
    float computedh = map(spectrum[i], 0, 1, 0, 200);
    computedh = min(computedh, 75);
    line(x + i * everyN, y, x + i * everyN, y - computedh);
  }

}

public void bigchart() {

  // keep moving the pen to the right
  // if we hit the edge, either go to the next row or clear the screen
  cursorx++;
  if (cursorx > width-margin-innermargin) {
    cursory+=rowheight+innermargin;
    cursorx = margin+innermargin;
    if (cursory>= (rowheight+innermargin)*2){
      cleanslate();
    }
  }

  // for each of our bands that we're sampling in our FFT, draw a little circle
  // (here, i'm drawing a circle within a slightly bigger, transparent circle to help blend overlapping marks)
  noStroke();
  ellipseMode(CENTER);
  int bandlim = round(bands*0.2f);
  for (int i=0; i<bandlim; i++) {
    float yyy = map( i, 0, bandlim, cursory+rowheight-innermargin_v, cursory+innermargin );
    float colora = map( spectrum[i], 0, 1, 0, 100 );
    float ellipsea = map( spectrum[i], 0, 1, 1, 15);
    fill(50, colora);
    ellipse(cursorx, yyy, ellipsea, ellipsea);
    fill(50, min(colora, 10));
    ellipse(cursorx, yyy, ellipsea*1.1f, ellipsea*1.1f);
  }

}

public void draw() {
  fft.analyze(spectrum);
  lilchart();
  bigchart();
}
  public void settings() {  size(1080, 1080); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "spectrogram_v1" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
