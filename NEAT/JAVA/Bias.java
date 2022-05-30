class Bias {
    // Provides Info about the historical origion of the connection.
    int innovation_number;
    
    // Value of the Bias
    float value;

    // Node the bias is atteched to
    int node;
  
    Bias(int node, float value, int innovation_number) {
        this.node = node;
        this.value = value;
        this.innovation_number = innovation_number;
    }
    
    // Creates a copy of the bias
    Bias Copy() {
        return new Bias(this.node, this.value, this.innovation_number);
    }

    @Override
    public String toString() {
        return "Node: " + this.node + ", Innovation Number: " + this.innovation_number + ", Value: " + this.value + "\n";
    }
}
