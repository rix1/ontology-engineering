
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

class LabelExtractor extends OWLObjectVisitorExAdapter<String> implements
        OWLAnnotationObjectVisitorEx<String> {

    public LabelExtractor() {
        super("");
    }


    @Nullable
    String result = null;

    @Override
    public String visit(@Nonnull OWLAnnotation node) {
        /*
         * If it's a label, grab it as the result. Note that if there are
         * multiple labels, the last one will be used.
         */
        if (node.getProperty().isLabel()) {
            OWLLiteral c = (OWLLiteral) node.getValue();
            result = c.getLiteral();
        }
        return "";
    }

    @Nullable
    public String getResult() {
        return result;
    }
}