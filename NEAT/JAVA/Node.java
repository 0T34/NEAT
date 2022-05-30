enum Nodetype {
    Input, Hidden, Output
}

class Node {
    // Identification number of the node 
    int id;

    // Gives Info about what the node is used for.
    Nodetype type;

    Node(int id, Nodetype type) {
        this.id = id;
        this.type = type;
    }

    Node Copy() {
        return new Node(this.id, this.type);
    }

    @Override
    public String toString() {
        return "ID: " + this.id + "\nNodetype: " + this.type + "\n";
    }
}
