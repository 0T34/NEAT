import java.util.ArrayList;

class InnovationMachine {
    int innovation;

    ArrayList<Connection> existingconnections;

    ArrayList<Bias> existingbiases;

    int startinnovation;
    
    // assigns the connection an innovation_number and adds the connection to the exisitingconnections if it doesnt already exist
    void GetInnovation(Connection c) {
        for (Connection con : this.existingconnections) {
            if (c.source_node_id == con.source_node_id && c.target_node_id == con.target_node_id) {
                c.innovation_number = con.innovation_number;
                return;
            }
        }

        c.innovation_number = this.innovation++;
        this.AddConnection(c.Copy());
    }

    // assigns the bias an innovation_number and adds the bias to the exisitingbiases if it doesnt already exist
    void GetInnovation(Bias b) {
        for (Bias bias : this.existingbiases) {
            if (bias.node == b.node) {
                b.innovation_number = bias.innovation_number;
                return;
            }
        }

        b.innovation_number = this.innovation++;
        this.AddBias(b.Copy());
    }
    
    void AddConnection(Connection c) {
        if (!this.ContainsConnection(c)) {
            this.existingconnections.add(c);
        }
    }
    
    boolean ContainsConnection(Connection contofind) {
        for (Connection c : this.existingconnections) {
            if (c.source_node_id == contofind.source_node_id && c.target_node_id == contofind.target_node_id) {
                return true;
            }
        }
        
        return false;
    }
    
    void AddBias(Bias b) {
        if (!this.ContainsBias(b)) {
            this.existingbiases.add(b);
        }
    }
    
    boolean ContainsBias(Bias biastofind) {
        for (Bias b : this.existingbiases) {
            if (b.node == biastofind.node) {
                return true;
            }
        }
        
        return false;
    }

    void Reset() {
        this.existingconnections = new ArrayList<Connection>();
        this.existingbiases = new ArrayList<Bias>();
        this.innovation = startinnovation;
    }

    InnovationMachine() {
        this(1);
    }
    
    InnovationMachine(int startinnovation) {
        this.startinnovation = startinnovation;
        Reset();
    }
}
