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

public class waveform extends PApplet {



// for live input
AudioIn in;

// for big chart
Waveform waveform;

// number of sample points to get from waveform
int samples = 300;

// number of neighbor samples to average together (for smoothing)
int samplearea = 10;

// copy of previous set of samples to additionally smooth against
float[] psamples = new float[samples];
float smoove = 0.25f;

// for lil' chart
FFT fft;
int bands = 128;
float[] spectrum = new float[bands];
int margin = round(1080*0.05f);
int innermargin = round(1080*0.05f);

public void setup() {
  
  // init with zeroes
  for (int i = 0; i < psamples.length; i++) {
     psamples[i] = 0;
  }
  // live input from system mic
  in = new AudioIn(this, 0);
  in.start();
  // analysis
  waveform = new Waveform(this, samples);
  waveform.input(in);
  fft = new FFT(this, bands);
  fft.input(in);
}

public float avgLast(int ind, int numsamples) {
    float sum = 0;
    for (int i = 0; i < numsamples; i++) {
      int index = ind - i;
      sum += psamples[index];
    }
    return sum / (float) numsamples;
}

public void draw() {
  background(255);
  fill(240);
  noStroke();
  rect(margin, margin, width-margin*2, height-margin*2);
  noFill();

  // layout debug
  /*
  stroke(150);
  strokeWeight(0.25);
  line(margin, margin, width-margin, margin);
  line(margin, height-margin, width-margin, height-margin);
  line(margin, margin, margin, height-margin);
  line(width-margin, margin, width-margin, height-margin);
  line(margin+(width-margin*2)*0.333, margin, margin+(width-margin*2)*0.333, height-margin);
  line(margin+(width-margin*2)*0.666, margin, margin+(width-margin*2)*0.666, height-margin);
  //*/

  // heavy line
  strokeCap(ROUND);
  strokeWeight(5);
  stroke(145);
  waveform.analyze();
  beginShape();
  for(int i = 0; i < samples; i++) {
    psamples[i] += (waveform.data[i]-psamples[i]) * smoove;
    if (i%samplearea==0) {
      float xx;
      if (i==0) {
        xx = psamples[i];
      } else {
        xx = avgLast(i, samplearea);
      }
      curveVertex(
        map(i, 0, samples, margin+innermargin-25, width-margin-innermargin+25),
        map(xx, -1, 1, margin, height-margin)
      );
    }
  }
  curveVertex(
    width-margin,
    map(psamples[samples-1], -1, 1, margin, height-margin)
  );
  endShape();

  // lil chart
  fft.analyze(spectrum);
  // put some air between lines
  int everyN = 4;
  // select just a portion of the spectrum to draw, for compositional balance
  int start = 0;
  int end = round(bands*0.5f);
  // dimensions
  float h = 400;
  float w = (end-start)*everyN;
  //draw individual lines
  stroke(145);
  for(int i = start; i < end; i++) {
    strokeWeight(1);
    float x = round((width - w) / 2);
    float y = height - margin - innermargin;
    float computedh = map(spectrum[i], 0, 1, 0, 200);
    line(x + i * everyN, y, x + i * everyN, y - computedh);
  }
}
  public void settings() {  size(1080, 1080); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "waveform" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
