/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.Random;

/**
 *
 * @author Bob
 */
public class SharkManager extends AbstractAppState {
    
  private SimpleApplication   app;
  private Random              rand;
  public  Node                sharkNode;
  private InteractionManager  inter;
  private boolean             up,down,left,right;
    
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app;
    inter    = app.getStateManager().getState(InteractionManager.class);
    init();
    setEnabled(false);
    }
  
  private void init(){
    sharkNode      = new Node();
    rand            = new Random();
    app.getRootNode().attachChild(sharkNode);
    }
  
  private void createSpider() {
    Shark spider     = new Shark();
    Node model       = (Node) app.getAssetManager().loadModel("Models/shark.j3o");
    AnimControl ac   = ((Node) model.getChild("Shark")).getChild("SharkBody").getControl(AnimControl.class);
    AnimChannel anch = ac.createChannel();
    anch.setAnim("Swim");
    model.setName("Model");
    spider.attachChild(model);
    sharkNode.attachChild(spider);
    randomizeSpider(spider);
    }
  
  private void randomizeSpider(Shark spider) {
      
    float x     = randInt(0, 50)-25;
    float y     = randInt(0, 50)-25;  
    
    x = x/2;
    y = y/2;
    
    if (x>y) {
        
      if (x>0) {
        x = 15.5f;
        spider.moveDir = new Vector3f(-1,0,0);
        spider.rotate(0,-89.5f,0);
        }
      
      else {
        x = -15.5f;
        spider.moveDir = new Vector3f(1,0,0);
        spider.rotate(0,89.5f,0);
        }
      
      }
    
    else {
        
      if (y>0) {
        y = 15.5f;
        spider.moveDir = new Vector3f(0,0,-1);
        spider.rotate(0,179f,0);
        }
      
      else {
        y = -15.5f;
        spider.moveDir = new Vector3f(0,0,1);
        }
      
      }
    
    spider.speed   = randInt(3,7);
    spider.moveDir = spider.moveDir.mult(spider.speed);
    spider.size    = (float) randInt(1,10)/10;
    spider.setLocalTranslation(x, 0, y);
    changeSize(spider);
    }
  
  private void changeSize(Shark spider){
    spider.setLocalScale(spider.size);
    }
  
  private int randInt(int min, int max) {
    int    randomNum = rand.nextInt((max - min) + 1) + min;
    return randomNum;
    }
  
  @Override
  public void update(float tpf) {
    
    //Creates a spider if there is less than 10 spiders  
    if (sharkNode.getQuantity() < 10) {
      createSpider();
      }
    
    //Checks Each Shark
    for (int i = 0; i < sharkNode.getQuantity(); i++) {
      
      //Checks the Interaction Manager for Input
      up    = inter.up;
      down  = inter.down;
      left  = inter.left;
      right = inter.right;
      
      //Sets players movement to 0
      float xMove = 0;
      float yMove = 0;
       
      //If there is any input, set the move accordingly 
      if (down) {
        yMove = 6;
        }
      
      else if (up) {
        yMove = -6;  
        }
      
      if (left) {
        xMove = -6;  
        }
      
      else if (right) {
        xMove = 6;  
        }
      
      //Gets the Current Shark
      Shark spider = (Shark) sharkNode.getChild(i);
      
      //Actually is doing the moving of the spider
      spider.move((spider.moveDir.add(xMove,0,yMove)).mult(tpf));
      
      //Remove the Shark if it is too far away
      if (spider.getLocalTranslation().x > 16 ^ spider.getLocalTranslation().x < -16)
      spider.removeFromParent();
      
      //Remove the Shark if it is too far away
      if (spider.getLocalTranslation().z > 16 ^ spider.getLocalTranslation().z < -16)
      spider.removeFromParent();
      
      //Checks each spider for collision with Current Shark
      for (int j = 0; j < sharkNode.getQuantity(); j++) {
       
        //Gets the Current Shark
        Shark currentSpider         = (Shark) sharkNode.getChild(j);
        CollisionResults results = new CollisionResults();
        
        //Checks to be sure it is not checking itself, then checks collision with current spider.
        if (spider != currentSpider) 
        spider.collideWith(currentSpider.getChild("Model").getWorldBound(), results);
        
        //Checks if a Shark is Hit
        if (results.size() > 0) {
          
          //If Current Shark is Bigger than the Hit Shark Make it Bigger and remove Hit Shark 
          if (currentSpider.size > spider.size) {
            spider.removeFromParent();
            if (currentSpider.size < .12) {
              currentSpider.size = spider.size + .1f;
              changeSize(currentSpider);
              }
            }
          
          //If Current Shark is Smaller than the Hit Shark Remove It and make Hit Shark Bigger
          else {
            currentSpider.removeFromParent();
            if (spider.size < .12) {
              spider.size = spider.size + .1f;
              changeSize(spider);
              }
            }
            
          }          
          
        }
      
      } 
    
    }
    
  }

