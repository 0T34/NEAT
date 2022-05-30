import java.util.ArrayList;
import java.util.Random;

class Genome {
    // Contains all of the Connection Genes. 
    ArrayList<Connection> connections;
    
    // Contains all of the Node Genes.
    ArrayList<Node> nodes;
    
    // Contains all of the Bias Genes.
    ArrayList<Bias> biases;
    
    // Contains the Number of Input Nodes.
    int inputs;
    
    // Contains the Number of Output Nodes.
    int outputs;

    // Just in case Mutation is impossible in one Network, there are a limited ammount of attempts so it doesn't keep trying infinitely.
    int max_mutation_attempts;
    
    // Represents the ID the node that is next added receives.
    int nextnodenumber = 1;

    // Returns the biggest innovation_number in the genome
    int GetMaxinnovation() {
        int maxinnovation = 0;
        
        // look through all of the connections
        for (Connection c : this.connections) {
            if (c.innovation_number > maxinnovation) {
                maxinnovation = c.innovation_number;
            }
        }
        
        // look through all the biases
        for (Bias b : this.biases) {
            if (b.innovation_number > maxinnovation) {
                maxinnovation = b.innovation_number;
            }
        }

        return maxinnovation;
    }

    Genome() {
        this.connections = new ArrayList<Connection>();
        this.biases = new ArrayList<Bias>();
        this.nodes = new ArrayList<Node>();
        this.nextnodenumber = 1;
    }

    Genome(int inputs, int outputs, int max_mutation_attempts) {
        this.connections = new ArrayList<Connection>();
        this.biases = new ArrayList<Bias>();
        this.nodes = new ArrayList<Node>();

        for (int i = 0; i < inputs; i++) {
            this.AddNode(Nodetype.Input);
        }

        for (int i = 0; i < outputs; i++) {
            this.AddNode(Nodetype.Output);
        }

        this.max_mutation_attempts = max_mutation_attempts;
    }

    Genome(ArrayList<Node> nodes, int max_mutation_attempts) {
        if (nodes != null) {
            this.nodes = nodes;
            for (Node n : this.nodes) {
                if (n.type == Nodetype.Input) {
                    this.inputs++;
                } else if (n.type == Nodetype.Output) {
                    this.outputs++;
                }
            }
        } else {
            this.nodes = new ArrayList<Node>();
        }

        this.connections = new ArrayList<Connection>();
        this.biases = new ArrayList<Bias>();
        this.nextnodenumber = this.nodes.size() + 1;
        this.max_mutation_attempts = max_mutation_attempts;
    }

    // Adds a Node and gives it an incremental ID and returns the ID. Warning: It is usually not a good idea to add an input or output node outside the constructor using this method.
    int AddNode(Nodetype type) {
        this.nodes.add(new Node(this.nextnodenumber, type));
        if (type == Nodetype.Input) {
            this.inputs++;
        } else if (type == Nodetype.Output) {
            this.outputs++;
        }

        return this.nextnodenumber++;
    }

    // Gets the node with the specified ID.
    Node GetNode(int id) {
        for (Node wn : this.nodes) {
            if (wn.id == id) {
                return wn;
            }
        }

        return null;
    }
    
    boolean CanAddConnection(Connection contoadd) {
        // if a gene with the same innovation_number already exists the connection cannot be added
        if (this.ContainsGene(contoadd.innovation_number)) {
            return false;
        }

        Node node1 = this.GetNode(contoadd.source_node_id);
        Node node2 = this.GetNode(contoadd.target_node_id); 

        // node/s don't exist
        if (node1 == null | node2 == null) {
            return false;
        } 

        // There can't be a connection from one node to itself.
        if (node1 == node2) {
            return false;
        }

        // An input node can't go into another input node
        // Same applies for output nodes.
        if ((node1.type == Nodetype.Input || node1.type == Nodetype.Output) && node1.type == node2.type) {
            return false;
        }
        
        // Check if the new connection would cause a circular feed
        if (this.CheckDependency(node1.id, node2.id)) {
            return false;
        }
        
        // A hidden or output node cannot connect back to the input layer
        if (node2.type == Nodetype.Input) {
            return false;
        }
        
        // An output node cannot connect back to the input or hidden layer
        if (node1.type == Nodetype.Output) {
            return false;
        }

        // if the connection to be added doesn't already exit it can be added
        if (!this.ContainsConnection(contoadd.source_node_id, contoadd.target_node_id)) {
            return true;
        } else {
            return false;
        }
    }
    
    // Adds a new Connection if allowed. This Method should only be used for modifying the start genome/copying a genome/other uses...
    boolean AddConnection(Connection contoadd) {
        if (this.CanAddConnection(contoadd)) {
            this.connections.add(contoadd);
            return true;
        } else {
            return false;
        }
    }
    
    // Adds a new Connection and assigns an incremental innovation_number. For this to work the Innovationnumber in contoadd has to be set to 0.
    boolean AddConnection(Connection contoadd, InnovationMachine im) {
        im.GetInnovation(contoadd); //assigns the connection an innovation_number
        if (this.CanAddConnection(contoadd)) {
            this.connections.add(contoadd);
            return true;
        } else {
            return false;
        }
    }
    
    // Returns true if the specified node already has a bias
    boolean ContainsBias(int nodeid) {
        for (Bias b : this.biases) {
            // if there is already a bias on the specified node
            if (b.node == nodeid) {
                return true;
            }
        }

        return false;
    }
    
    // Gets the bias from the specified node
    Bias GetBias(int nodeid) {
        for (Bias b : this.biases) {
            if (b.node == nodeid) {
                return b;
            }
        }

        return null;
    }
    
    // Gets the Bias with the specified innovation number
    Bias GetBiasGene(int innovation_number) {
        for (Bias b : this.biases) {
            if (b.innovation_number == innovation_number) {
                return b;
            }
        }

        return null;
    }
    
    // Checks if it is possible to add the bias
    boolean CanAddBias(Bias biastoadd) {
        return !this.ContainsBias(biastoadd.node) && !this.ContainsGene(biastoadd.innovation_number);
    }
    
    // Adds a new Bias. This method should only be used for creating a startgenome/copying a genome/...
    boolean AddBias(Bias b) {
        if (this.CanAddBias(b)) {
            this.biases.add(b);
            return true;
        } else {
            return false;
        }
    }
    
    // Adds a bias and assigns an incremental innovation_number. For this to work b has to have an innovation_number of 0.
    boolean AddBias(Bias b, InnovationMachine im) {
        im.GetInnovation(b);
        if (this.CanAddBias(b)) {
            this.biases.add(b);
            return true;
        } else {
            return false;
        }
    }

    // Checks if there is already an existing connection with matching source and target node
    boolean ContainsConnection(int source, int target) {
        Connection connectionToCompareTo = new Connection(source, target, true, 0, 0);
        for (Connection c : this.connections) {
            if (c.source_node_id == connectionToCompareTo.source_node_id && c.target_node_id == connectionToCompareTo.target_node_id) {
                return true;
            }
        }

        return false;
    }

    // Returns true if node1 directly or indirectly received input from node2.
    boolean CheckDependency(int node1, int node2) {
        boolean isDependantOnNode2 = false;

        // Find all the connections to node1...
        ArrayList<Connection> connectionsToNode1 = FindConnectionsToNode(node1);
        for (Connection cg : connectionsToNode1) {
            if (cg.is_expressed) {
                // ...find the source/neighbour nodes from that connection
                Node neighbourNode = GetNode(cg.source_node_id);

                // ...if one of those nodes has node2 as their id, then node1 is dependant on node2
                if (neighbourNode.id == node2) {
                    return true;
                }

                // do this recursively so indirect dependencies can be found
                isDependantOnNode2 = CheckDependency(neighbourNode.id, node2);

                if (isDependantOnNode2) {
                    return true;
                }
            }
        }

        return isDependantOnNode2;
    }

    // Finds all the Connections that go into the specified Node.
    ArrayList<Connection> FindConnectionsToNode(int id) {
        ArrayList<Connection> connectionsToNode = new ArrayList<Connection>();
        for (Connection cg : this.connections) {
            if (cg.target_node_id == id) {
                connectionsToNode.add(cg);
            }
        }

        return connectionsToNode;
    }

    // Finds all the Connections that go out of the specified Node.
    ArrayList<Connection> FindConnectionsFromNode(int number) {
        ArrayList<Connection> connectionsFromNode = new ArrayList<Connection>();
        for (Connection cg : this.connections) {
            if (cg.source_node_id == number) {
                connectionsFromNode.add(cg);
            }
        }

        return connectionsFromNode;
    }

    /// Returns a ArrayList of all Nodes that belong to the same Nodetype as specified.
    ArrayList<Node> FindNodesOfType(Nodetype type) {
        ArrayList<Node> ngs = new ArrayList<Node>();
        for (Node ng : this.nodes) {
            if (ng.type == type) {
                ngs.add(ng);
            }
        }

        return ngs;
    }

    /// Replaces an existing connection with a new node and adds two connections.
    void AddNodeMutation(InnovationMachine im, Random r) {
        if (this.connections.size() == 0) {
            // no connections to replace
            return;
        }

        // Whenever a new Node is added, a previous Connection is replaced.
        int attemptcount = 1;
        Connection connectionToReplace = null;

        do {
            Connection randomConnection = this.connections.get(Math.round(r.nextFloat() * (this.connections.size() - 1)));
            if (randomConnection != null && randomConnection.is_expressed) {
                connectionToReplace = randomConnection;
            }
        } while (connectionToReplace != null && attemptcount++ < this.max_mutation_attempts);

        if (connectionToReplace == null) {
            return; // no connection to replace found
        }

        int newNodeID = this.AddNode(Nodetype.Hidden);

        // The connection leading into the new node has a weight of 1, and the on out a weight the weight of the old connection
        ArrayList<Connection> connectionstoadd = new ArrayList<Connection>();

        connectionstoadd.add(new Connection(connectionToReplace.source_node_id, newNodeID, true, 0, 1));
        connectionstoadd.add(new Connection(newNodeID, connectionToReplace.target_node_id, true, 0, connectionToReplace.weight));

        for (Connection connectiontoadd : connectionstoadd) {
            this.AddConnection(connectiontoadd, im); // Add the new connections
        }

        connectionToReplace.is_expressed = false;
    }

    // Randomly adjusts the weights of the genome
    void AdjustweightMutation(Random r) {
        for (Connection c : this.connections) {
            float adjustionType = r.nextFloat();
            if (adjustionType < 0.1f) {
                // Assign the connection a random value between -1 and 1
                c.weight = r.nextFloat() * 2 - 1;
            } else {
                // Nudge the existing value
                c.weight += r.nextGaussian() / 10;

                if (c.weight > 1) {
                    c.weight = 1;
                } else if (c.weight < -1) {
                    c.weight = -1;
                }
            }
        }
    }
    
    // Adds a new random connection
    void AddConnectionMutation(InnovationMachine im, Random r) {
        if (this.nodes.size() <= 0) {
            // not enough nodes to add a connection
            return;
        }

        int attempts = 1;
        do {
            // find random nodes for the connection
            int from = Math.round(r.nextFloat() * (this.nodes.size() - 1)) + 1;
            int to = Math.round(r.nextFloat() * (this.nodes.size() - 1)) + 1;
            if (from == to) {
                continue;
            }

            Connection conToAdd = new Connection(from, to, true, 0, r.nextFloat() * 2 - 1);
            if (this.AddConnection(conToAdd, im)) {
                return; // if a connection was succesfully added then return
            }
        } while (attempts++ < this.max_mutation_attempts);
    }
    
    // adds a new random bias
    void AddBiasMutation(InnovationMachine im, Random r) {
        if (this.nodes.size() == 0) {
            return; // no nodes to add biases to
        }

        int attempts = 1;
        do {
            int node = Math.round(r.nextFloat() * (this.nodes.size() - 1)) + 1; // find a random node
            Bias biastoadd = new Bias(node, r.nextFloat() * 2 - 1, 0);
            if (this.AddBias(biastoadd, im)) {
                return; // if the bias was added succesfully then return
            }
        } while (attempts++ < this.max_mutation_attempts);
    }

    // adjusts the existing biases
    void AdjustBiasMutation(Random r) {
        for (Bias biastoadjust : this.biases) {
            float adjustionType = r.nextFloat();
            if (adjustionType < 0.1f) {
                // Assign the bias a random value between -1 and 1 10% of the time
                biastoadjust.value = r.nextFloat() * 2 - 1;
            } else {
                // Nudge the existing value 90% of the time
                biastoadjust.value += r.nextGaussian() / 10;

                // keep the value within a certain range
                if (biastoadjust.value > 1) {
                    biastoadjust.value = 1;
                } else if (biastoadjust.value < -1) {
                    biastoadjust.value = -1;
                }
            }
        }
    }

    // enables/disables a random connection
    void EnableDisableConnectionMutation(Random r) {
        if (this.connections.size() == 0) {
            return; // no connections to enable/disable
        }

        Connection connectionToToggle = this.connections.get(Math.round(r.nextFloat() * (this.connections.size() - 1)));
        if (connectionToToggle != null) {
            // if a connection was found then toggle isexpressed
            connectionToToggle.is_expressed = !connectionToToggle.is_expressed;
        }
    }

    // returns if there is already a connection or bias gene with the specified innovation_number
    boolean ContainsGene(int innovation_number) {
        for (Connection c : this.connections) {
            if (c.innovation_number == innovation_number) {
                return true;
            }
        }

        for (Bias b : this.biases) {
            if (b.innovation_number == innovation_number) {
                return true;
            }
        }

        return false;
    }

    // gets the connectiongene with the specified innovation_number
    Connection GetConnectionGene(int innovation_number) {
        for (Connection c : this.connections) {
            if (c.innovation_number == innovation_number) {
                return c;
            }
        }

        return null;
    }

    // returns a copy of the genome
    Genome Copy() {
        ArrayList<Node> nodescopy = new ArrayList<Node>();
        for (Node n : this.nodes) {
            nodescopy.add(n.Copy());
        }

        Genome g = new Genome(nodescopy, this.max_mutation_attempts);
        for (Connection c : this.connections) {
            g.AddConnection(new Connection(c.source_node_id, c.target_node_id, c.is_expressed, c.innovation_number, c.weight));
        }

        for (Bias b : this.biases) {
            g.AddBias(new Bias(b.node, b.value, b.innovation_number));
        }

        return g;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Input Nodes: " + this.inputs + " Output Nodes: " + this.outputs + "\n");
        sb.append("Max Mutation Attempts: " + this.max_mutation_attempts + "\n");
        
        sb.append("Nodes:\n");
        for (Node wn : this.nodes) {
            sb.append(wn.toString());
        }

        sb.append("\n" + "Connections:\n");
        for (Connection con : this.connections) {
            sb.append(con.toString());
        }

        sb.append("\n" + "Biases:\n");
        for (Bias b : this.biases) {
            sb.append(b.toString());
        }

        return sb.toString();
    }
}
