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
import java.io.File;
import java.io.PrintStream;
import javax.annotation.Nonnull;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

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

    private SimpleHierarchyExample(@Nonnull OWLOntology inputOntology) {
        this.ontology = inputOntology;
        out = System.out;
    }

    /**
     * Print the class hierarchy for the given ontology from this class down,
     * assuming this class is at the given level. Makes no attempt to deal
     * sensibly with multiple inheritance.
     */
    private void printHierarchy(@Nonnull OWLClass clazz) throws OWLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        OWLReasoner reasoner = getReasoner(ontology);
        printHierarchy(reasoner, clazz, 0);
        /* Now print out any unsatisfiable classes */
        for (OWLClass cl : ontology.getClassesInSignature()) {
            assert cl != null;
            if (!reasoner.isSatisfiable(cl)) {
                out.println("XXX: " + labelFor(cl));
            }
        }
        reasoner.dispose();
    }


    private String labelFor(@Nonnull OWLClass clazz) {
        /*
         * Use a visitor to extract label annotations
         */
        LabelExtractor le = new LabelExtractor();
        for (OWLAnnotation anno : annotations(ontology
                .getAnnotationAssertionAxioms(clazz.getIRI()))) {
            anno.accept(le);
        }
        /* Print out the label if there is one. If not, just use the class URI */
        if (le.getResult() != null) {
            return le.getResult();
        } else {
            return clazz.getIRI().toString();
        }
    }


    /**
     * Print the class hierarchy from this class down, assuming this class is at
     * the given level. Makes no attempt to deal sensibly with multiple
     * inheritance.
     */
    private void printHierarchy(@Nonnull OWLReasoner reasoner,
                                @Nonnull OWLClass clazz, int level) throws OWLException {
        /*
         * Only print satisfiable classes -- otherwise we end up with bottom
         * everywhere
         */
        if (reasoner.isSatisfiable(clazz)) {
            for (int i = 0; i < level * INDENT; i++) {
                out.print(" ");
            }
            out.println(labelFor(clazz));
            /* Find the children and recurse */
            for (OWLClass child : reasoner.getSubClasses(clazz, true)
                    .getFlattened()) {
                if (!child.equals(clazz)) {
                    printHierarchy(reasoner, child, level + 1);
                }
            }
        }
    }


    public static void main(String[] args) throws  Exception { // OWLException, InstantiationException, IllegalAccessException, ClassNotFoundException
        // We first need to obtain a copy of an
        // OWLOntologyManager, which, as the name
        // suggests, manages a set of ontologies.
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        // We load an ontology from the URI specified
        // on the command line
//        String x = args[0];
//        System.out.println(x);
//        IRI documentIRI = IRI.create(x);
//        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(documentIRI);

        // Now load the ontology.

        File file = new File(PATH + "pizza.owl");
        IRI documentIRI = IRI.create(file);

        Render render = new Render();

        render.render(documentIRI);

        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(documentIRI);


        // Report information about the ontology
        System.out.println("Ontology Loaded...");
        System.out.println("Document IRI: " + documentIRI);
        System.out.println("Ontology : " + ontology.getOntologyID());
        System.out.println("Format      : " + manager.getOntologyFormat(ontology));

        // / Create a new SimpleHierarchy object with the given reasoner.
        @SuppressWarnings("null")
        SimpleHierarchyExample simpleHierarchy = new SimpleHierarchyExample(ontology);
        // Get Thing
        OWLClass clazz = manager.getOWLDataFactory().getOWLThing();
        System.out.println("Class       : " + clazz);
        // Print the hierarchy below thing
        simpleHierarchy.printHierarchy(clazz);
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