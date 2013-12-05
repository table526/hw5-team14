

/* First created by JCasGen Wed Dec 04 14:09:18 EST 2013 */
package edu.cmu.lti.qalab.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Dec 04 14:51:55 EST 2013
 * XML source: /home/pan/11791/hw5/qa4mre-base/src/main/resources/TypeSystemDescriptor.xml
 * @generated */
public class Verb extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Verb.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Verb() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Verb(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Verb(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Verb(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets 
   * @generated */
  public String getText() {
    if (Verb_Type.featOkTst && ((Verb_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.qalab.types.Verb");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Verb_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets  
   * @generated */
  public void setText(String v) {
    if (Verb_Type.featOkTst && ((Verb_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.qalab.types.Verb");
    jcasType.ll_cas.ll_setStringValue(addr, ((Verb_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: weight

  /** getter for weight - gets 
   * @generated */
  public double getWeight() {
    if (Verb_Type.featOkTst && ((Verb_Type)jcasType).casFeat_weight == null)
      jcasType.jcas.throwFeatMissing("weight", "edu.cmu.lti.qalab.types.Verb");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Verb_Type)jcasType).casFeatCode_weight);}
    
  /** setter for weight - sets  
   * @generated */
  public void setWeight(double v) {
    if (Verb_Type.featOkTst && ((Verb_Type)jcasType).casFeat_weight == null)
      jcasType.jcas.throwFeatMissing("weight", "edu.cmu.lti.qalab.types.Verb");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Verb_Type)jcasType).casFeatCode_weight, v);}    
   
    
  //*--------------*
  //* Feature: synonym

  /** getter for synonym - gets 
   * @generated */
  public FSList getSynonym() {
    if (Verb_Type.featOkTst && ((Verb_Type)jcasType).casFeat_synonym == null)
      jcasType.jcas.throwFeatMissing("synonym", "edu.cmu.lti.qalab.types.Verb");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Verb_Type)jcasType).casFeatCode_synonym)));}
    
  /** setter for synonym - sets  
   * @generated */
  public void setSynonym(FSList v) {
    if (Verb_Type.featOkTst && ((Verb_Type)jcasType).casFeat_synonym == null)
      jcasType.jcas.throwFeatMissing("synonym", "edu.cmu.lti.qalab.types.Verb");
    jcasType.ll_cas.ll_setRefValue(addr, ((Verb_Type)jcasType).casFeatCode_synonym, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    