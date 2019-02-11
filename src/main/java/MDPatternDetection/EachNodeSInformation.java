package MDPatternDetection;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;

public class EachNodeSInformation {


    private Resource subject;
    private int numberAsSubject;
    private int numberAsObject;
    private ArrayList<Statement> listAsSubject = new ArrayList<>();
    private ArrayList<Statement> listAsObject = new ArrayList<>();

    public EachNodeSInformation(Resource subject, int numberAsSubject, int numberAsObject, Statement statementAsSubject, Statement statementAsObject) {
        this.subject = subject;
        this.numberAsSubject = numberAsSubject;
        this.numberAsObject = numberAsObject;
        if (statementAsSubject != null) this.listAsSubject.add(statementAsSubject);
        if (statementAsObject != null) this.listAsObject.add(statementAsObject);
    }

    public EachNodeSInformation(Resource subject, int numberAsSubject, int numberAsObject) {
        this.subject = subject;
        this.numberAsSubject = numberAsSubject;
        this.numberAsObject = numberAsObject;
    }

    public Resource getSubject() {
        return subject;
    }

    public void setSubject(Resource subject) {
        this.subject = subject;
    }

    public int getNumberAsSubject() {
        return numberAsSubject;
    }

    public void setNumberAsSubject(int numberAsSubject) {
        this.numberAsSubject = numberAsSubject;
    }

    public int getNumberAsObject() {
        return numberAsObject;
    }

    public void setNumberAsObject(int numberAsObject) {
        this.numberAsObject = numberAsObject;
    }

    public ArrayList<Statement> getListAsSubject() {
        return listAsSubject;
    }

    public void setListAsSubject(ArrayList<Statement> listAsSubject) {
        this.listAsSubject = listAsSubject;
    }

    public ArrayList<Statement> getListAsObject() {
        return listAsObject;
    }

    public void setListAsObject(ArrayList<Statement> listAsObject) {
        this.listAsObject = listAsObject;
    }

    public void setNumberAsSubject() {
        this.numberAsSubject++;
    }

    public void setNumberAsObject() {
        this.numberAsObject++;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Resource) {
            return subject.equals(obj);
        } else return false;

    }
}
