//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.23 at 09:36:22 AM CET 
//


package org.junit.tools.reports.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.junit.tools.reports.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.junit.tools.reports.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RepProject }
     * 
     */
    public RepProject createRepProject() {
        return new RepProject();
    }

    /**
     * Create an instance of {@link Statistics }
     * 
     */
    public Statistics createStatistics() {
        return new Statistics();
    }

    /**
     * Create an instance of {@link NewMethods }
     * 
     */
    public NewMethods createNewMethods() {
        return new NewMethods();
    }

    /**
     * Create an instance of {@link RepPackage }
     * 
     */
    public RepPackage createRepPackage() {
        return new RepPackage();
    }

    /**
     * Create an instance of {@link RepMethod }
     * 
     */
    public RepMethod createRepMethod() {
        return new RepMethod();
    }

    /**
     * Create an instance of {@link RepClass }
     * 
     */
    public RepClass createRepClass() {
        return new RepClass();
    }

    /**
     * Create an instance of {@link Report }
     * 
     */
    public Report createReport() {
        return new Report();
    }

}
