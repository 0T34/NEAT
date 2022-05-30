class Connection {
    // Represents the weight of the Connection.
    float weight;

    // Indicates where the Connection starts.
    int source_node_id;

    // Indicates where the Connection ends.
    int target_node_id;
    
    // Provides Info about the historical origion of the connection.
    int innovation_number;

    // Gives information about whether or not the connection is enabled
    boolean is_expressed;

    Connection() {
        this(-1, -2, false, -1, 0.0f);
    }

    Connection(int source_node_id, int target_node_id, boolean isexpressed, int innovation_number, float value) {
        this.source_node_id = source_node_id;
        this.target_node_id = target_node_id;
        this.is_expressed = isexpressed;
        this.innovation_number = innovation_number;
        this.weight = value;
    }

    Connection Copy() {
        return new Connection(this.source_node_id, this.target_node_id, this.is_expressed, this.innovation_number, this.weight);
    }

    @Override
    public String toString() {
        return " SourcenodeID: " + this.source_node_id +
               " TargetnodeID: " + this.target_node_id +
               " is_expressed: " + this.is_expressed +
               " Innovationnumber: " + this.innovation_number +
               " value: " + this.weight +
               "\n";
    }
}
