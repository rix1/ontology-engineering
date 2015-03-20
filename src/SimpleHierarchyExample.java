/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */

import static org.semanticweb.owlapi.search.Searcher.annotations;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.xml.sax.SAXException;

/**
 * Simple example. Read an ontology, and display the class hierarchy. May use a
 * reasoner to calculate the hierarchy.
 *
 * @author Sean Bechhofer, The University Of Manchester, Information Management
 *         Group
 * @since 2.0.0
 */
@SuppressWarnings("javadoc")
public final class SimpleHierarchyExample {

    public static final String PATH = "/Users/rikardeide/Development/ontology-engineering/temp/";
    private static final int INDENT = 4;
    private final OWLOntology ontology;
    private final PrintStream out;
    private static XMLparserTakeTwo parser;
    private Constraint con;
    private int counter = 0;
    private String verbalization;

    public static boolean DEBUG = false;

    private SimpleHierarchyExample(@Nonnull OWLOntology inputOntology) {
        this.ontology = inputOntology;
        out = System.out;
    }

    /**
     * Print the class hierarchy for the given ontology from this class down,
     * assuming this class is at the given level. Makes no attempt to deal
     * sensibly with multiple inheritance.
     */


    private String labelFor(@Nonnull OWLClass clazz) {
        /*
         * Use a visitor to extract label annotations
         */
        LabelExtractor le = new LabelExtractor();
        for (OWLAnnotation anno : annotations(ontology.getAnnotationAssertionAxioms(clazz.getIRI()))) {
            anno.accept(le);
        }
        /* Print out the label if there is one. If not, just use the class URI */
        if (le.getResult() != null) {
            return le.getResult();
        } else {
//            return clazz.getIRI().toString();
            return "";
        }
    }


    private String getName(@Nonnull OWLClass claz) {
        String temp = claz.getIRI().toString();
        return temp.split("#")[1];
    }

    private boolean testLabelFor(@Nonnull OWLClass clz){
        String test = labelFor(clz);

        return !test.equals("") || !test.equals(" ");
//        return true;
    }


    /**
     * Verbalizes Subclasses
     */
    private String verbalizeSubClasses(@Nonnull OWLReasoner reasoner, @Nonnull OWLClass parent)
            throws OWLException, IOException, SAXException, ParserConfigurationException {
        /*
         * Only print satisfiable classes -- otherwise we end up with bottom
         * everywhere
         */

        if (reasoner.isSatisfiable(parent)) {

            /* Find the children and recurse */
            for (OWLClass child : reasoner.getSubClasses(parent, true).getFlattened()) {
                if (!child.equals(parent)) {
                    if(testLabelFor(parent)){
                        verbalization += "\n" + con.createVerbalization(labelFor(child), labelFor(parent));
                        System.out.println(con.createVerbalization(labelFor(child), labelFor(parent)) + "<br>");
//                        System.out.println(child.containsConjunct()); // Did not finish this...
                    }
                    verbalizeSubClasses(reasoner, child);
                }
            }
        }
        return verbalization;
    }

    private void verbalizeDisjointClasses(@Nonnull OWLReasoner reasoner, @Nonnull OWLClass parent){
        if(reasoner.isSatisfiable(parent)){

            HashSet nodeSet = (HashSet) reasoner.getDisjointClasses(parent).getFlattened();

            System.out.println("disjoint " + nodeSet.toString());

            Iterator it = nodeSet.iterator();

            System.out.println("\n## " + getName(parent) + " :");

            while(it.hasNext()){
                OWLClass ff = (OWLClass)it.next();
                System.out.println(con.createVerbalization(getName(ff), getName(parent)));
//                System.out.println(getName(ff));
//                System.out.println(ff.getIRI().toString());
            }

            Set<OWLClass> classes = reasoner.getSubClasses(parent, false).getFlattened();
            System.out.println(classes.size());

            if(counter < 3) {
                for (OWLClass child : classes) {
                    if (!child.equals(parent)) {
                        verbalizeDisjointClasses(reasoner, child);
                        counter++;
                    }
//                System.out.println(child.toStringID());
                }
            }

            // Still havent figured out how disjoint classes work..
//            System.out.println(Arrays.toString(a));

//            System.out.println(nodeSet.getNodes().size());

//            for(OWLClass child : reasoner.getDisjointClasses(parent).getFlattened()){
//                if(!child.equals(parent)){
//                    System.out.println(con.createVerbalization(labelFor(child), labelFor(parent)));
//                }
//                verbalizeDisjointClasses(reasoner, child);
//            }
        }
    }

    public boolean writeToFile(String contents) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("temp/output.txt", "utf-8");
        writer.write(contents);
        writer.close();
        return true;
    }

    /**
     * V.0.1
     * Select what operation to do by passing arguments.
     * This sets the constraint (cant figure out a better name) and initiates the correct method
     * */

    public void startVerbalizer(@Nonnull OWLClass clazz) throws IllegalAccessException, InstantiationException, IOException, ClassNotFoundException, SAXException, ParserConfigurationException, OWLException {
        OWLReasoner reasoner = getReasoner(ontology);
        setConstraint(XMLparserTakeTwo.SUBSUMPTION); // Select type here

        verbalization = "";
        verbalization += verbalizeSubClasses(reasoner, clazz);
//        verbalizeDisjointClasses(reasoner, clazz);


//        writeToFile(verbalization);

        print("File written to disk. Disposing...");

        /* Now print out any unsatisfiable classes */
        for (OWLClass cl : ontology.getClassesInSignature()) {
            assert cl != null;
            if (!reasoner.isSatisfiable(cl)) {
                out.println("XXX: " + labelFor(cl));
            }
        }
        reasoner.dispose();
    }

    public void setConstraint(String type){
        con = parser.getSchema(type);
        print(con.toString());
    }


    private void getProperty(@Nonnull OWLClass clazz)
            throws OWLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        /*
         * Only print satisfiable classes -- otherwise we end up with bottom
         * everywhere
         */

        OWLReasoner reasoner = getReasoner(ontology);

        if (reasoner.isSatisfiable(clazz)) {

            // Find children that are subclasses
//            for (OWLClass child : reasoner.getSubClasses(clazz, true).getFlattened()) {
            NodeSet subClasses = reasoner.getSubClasses(clazz, false);
            NodeSet disjointClasses = reasoner.getDisjointClasses(clazz);
//            NodeSet objectVals = reasoner.getEquivalentClasses(clazz); This is done on a Node level


            System.out.println("HERE:\n" + Arrays.toString(subClasses.getNodes().toArray()));

//            for (OWLClass child : reasoner.getSubClasses(clazz, true).getFlattened()) {
//                if (!child.equals(clazz)) {
//                    System.out.println("Here:\n");
//                    System.out.println(child.toString());
//                }
//            }

        }

    }


    public static void main(String[] args) throws  Exception { // OWLException, InstantiationException, IllegalAccessException, ClassNotFoundException

        /*
        * ARGS:
        * 0: URL to OWL Ontology
        * 1: Path to XML rules
        *
        * */

        String xmlSchema = PATH + "siste.xml";
        String ontologyURI = PATH + "familie.owl";

        if (args.length > 0) {
            ontologyURI = args[0];
            if(args.length > 1){
                xmlSchema = args[1];
            }
        }

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

//        File file = new File(PATH + "familie.owl");
        IRI documentIRI = IRI.create(ontologyURI);

        // Add arguments on startup


        // Writes to a file with functional syntax
//        Render render = new Render();
//        render.render(documentIRI);

        parser = new XMLparserTakeTwo();
        parser.loadDocument(xmlSchema);


        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(documentIRI);

        // Report information about the ontology
        print("Ontology Loaded...");
        print("Document IRI: " + documentIRI);
        print("Ontology : " + ontology.getOntologyID());
        print("Format      : " + manager.getOntologyFormat(ontology));

        // Create a new SimpleHierarchy object with the given reasoner.
        SimpleHierarchyExample simpleHierarchy = new SimpleHierarchyExample(ontology);
        // Get Thing
        OWLClass clazz = manager.getOWLDataFactory().getOWLThing();
        print("Class       : " + clazz);

        // Print the hierarchy below thing
        simpleHierarchy.startVerbalizer(clazz);
//        simpleHierarchy.printHierarchy(clazz);
//        simpleHierarchy.getProperty(clazz);
    }


    public static void print(String s){
        if(DEBUG){
            System.out.println("DEBUG: "+s);
        }
    }


    public static OWLReasoner getReasoner(OWLOntology ontology) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        String reasonerFactoryName;
        reasonerFactoryName = "uk.ac.manchester.cs.jfact.JFactFactory"; // For api-level 4
//        reasonerFactoryName = "org.semanticweb.elk.owlapi.ElkReasonerFactory";
//        reasonerFactoryName = "org.semanticweb.HermiT.Reasoner$ReasonerFactory";

        OWLReasonerFactory reasonerFactory = (OWLReasonerFactory) Class.forName(reasonerFactoryName).newInstance();
        reasonerFactory.createReasoner(ontology);


        return reasonerFactory.createReasoner(ontology);
    }
}