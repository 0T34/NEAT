import java.util.ArrayList;
import java.util.HashMap;

class Network {
    Genome genome;

    HashMap<Integer, Float> outputDataAtNode;

    HashMap<Integer, Boolean> hasBeenCalculatedAtNode;

    Network(Genome genome) {
        this.genome = genome;
    }

    // Feeds all of the Input into the Network and calculates all of the Output at the Output Node.
    float[] FeedForward(float[] input) {
        hasBeenCalculatedAtNode = new HashMap<Integer, Boolean>();
        outputDataAtNode = new HashMap<Integer, Float>();

        int currentinput = 0;
        for (Node ng : this.genome.nodes) {
            if (ng.type == Nodetype.Input) {
                outputDataAtNode.put(ng.id, input[currentinput++]);

                Bias b = this.genome.GetBias(ng.id);
                if (b != null) {
                    outputDataAtNode.put(ng.id, outputDataAtNode.get(ng.id) + b. value); // ---------
                }

                hasBeenCalculatedAtNode.put(ng.id, true);
            }
        }

        ArrayList<Node> outputnodes = this.genome.FindNodesOfType(Nodetype.Output);
        float[] output = new float[outputnodes.size()];

        for (int i = 0; i < outputnodes.size(); i++) {
            // Calculates the output of the specified node.
            output[i] = CalculateOutputAtNode(outputnodes.get(i).id, input);
        }

        return output;
    }

    // Calculates the Output at the specified Node.
    float CalculateOutputAtNode(int number, float[] input) {
        // if the input has already been calculated and saved it can just be returned.
        if (hasBeenCalculatedAtNode.containsKey(number) && hasBeenCalculatedAtNode.get(number)) {
            return outputDataAtNode.get(number);
        }

        ArrayList<Connection> connectionsToNode = this.genome.FindConnectionsToNode(number);

        // Add a new Entry into the dictionary for the input data if it doesn't alreay exist
        if (!outputDataAtNode.containsKey(number)) {
            outputDataAtNode.put(number, 0.0f);
        }

        for (Connection con : connectionsToNode) {
            //Adds up all of the inputs that go into this node. This can be done recursively.
            if (con.is_expressed) {
                outputDataAtNode.put(number, outputDataAtNode.get(number) + CalculateOutputAtNode(con.source_node_id, input) * con.weight);
            }
        }

        Bias b = this.genome.GetBias(number);
        if (b != null) {
            outputDataAtNode.put(number, outputDataAtNode.get(number) + b.value);
        }

        outputDataAtNode.put(number, Sigmoid(outputDataAtNode.get(number)));

        // For efficiency this keeps track of if we already calculated the value at the given node.
        if (!hasBeenCalculatedAtNode.containsKey(number)) {
            hasBeenCalculatedAtNode.put(number, true);
        }

        return outputDataAtNode.get(number);
    }

    // TODO: Add more activation functions later
    // Sigmoid is the activation function
    float Sigmoid(float x) {
        return (float)1 / (float)(1 + (float)Math.exp(-4.9f * x));
    }
}
