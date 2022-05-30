import java.util.ArrayList;
import java.util.Random;

enum Parent {
    G1, G2
}
  
class GenomeHelper {
    // returns how compatible 2 genomes are, so that they can be placed into species
    static float GetCompatibilityDistance(Genome g1, Genome g2, float excessimportance, float disjointimportance, float averageweightdifferenceimportance) {
        if (g1 == null || g2 == null) {
            return Float.MAX_VALUE; // if both are null just return the max possible number because they cannot be compatible at all
        }
        
        // get all of the excess and disjoint genes for their count
        // could be optimised with a getexcess/dijointcount method

        ArrayList<Connection> excessconns = GenomeHelper.GetExcessConnections(g1, g2, Parent.G1);
        excessconns.addAll(GenomeHelper.GetExcessConnections(g1, g2, Parent.G2));
        
        ArrayList<Bias> excessbias = GenomeHelper.GetExcessBiases(g1, g2, Parent.G1);
        excessbias.addAll(GenomeHelper.GetExcessBiases(g1, g2, Parent.G2));

        ArrayList<Connection> disjointconns = GenomeHelper.GetDisjointConnections(g1, g2, Parent.G1);
        disjointconns.addAll(GenomeHelper.GetDisjointConnections(g1, g2, Parent.G2));
        
        ArrayList<Bias> disjointbias = GenomeHelper.GetDisjointBiases(g1, g2, Parent.G1);
        disjointbias.addAll(GenomeHelper.GetDisjointBiases(g1, g2, Parent.G2));
        
        int excessgenecount = excessconns.size() + excessbias.size();
        int disjointgenecount = disjointconns.size() + disjointbias.size();
        
        int g1genecount = g1.connections.size() + g1.biases.size();
        int g2genecount = g2.connections.size() + g2.biases.size();

        int N = g1genecount > g2genecount ? g1genecount : g2genecount;
        if (N < 20) {
            // if the genomes are small 1 can be used for the ammount
            N = 1;
        }
        
        //calculate the compatiblity distance
        float temp = excessimportance * (excessgenecount) / N + disjointimportance * (disjointgenecount) / N 
        + averageweightdifferenceimportance * GetAverageweightDifference(g1, g2);

        return temp;
    }
    
    // returns all of the disjoint connection genes
    static ArrayList<Connection> GetDisjointConnections(Genome g1, Genome g2, Parent parent) {
        ArrayList<Connection> connectiongenes = new ArrayList<Connection>();

        if (g1 == null || g2 == null) {
            return connectiongenes;
        }

        // g1 has to be the larger genome
        if (g2.GetMaxinnovation() > g1.GetMaxinnovation()) {
            Genome temp = g1;
            g1 = g2;
            g2 = temp;
        }

        Genome gtofinddisjointsfrom;
        Genome otherg;
        
        // set what parents to get the disjoint genes from
        if (parent == Parent.G1) {
            gtofinddisjointsfrom = g1;
            otherg = g2;
        } else {
            otherg = g1;
            gtofinddisjointsfrom = g2;
        }

        for (Connection c : gtofinddisjointsfrom.connections) {
            if (!otherg.ContainsGene(c.innovation_number)) {
                // if a gene is within the innovationrange of the smaller genome and it doesnt exist in the other genome its a disjoint gene
                if (c.innovation_number <= g2.GetMaxinnovation()) {
                    connectiongenes.add(c);
                }
            }
        }
        
        return connectiongenes;
    }
    
    // returns all of the disjoint bias genes
    static ArrayList<Bias> GetDisjointBiases(Genome g1, Genome g2, Parent parent) {
        ArrayList<Bias> biasgenes = new ArrayList<Bias>();

        if (g1 == null || g2 == null) {
            return biasgenes;
        }

        // g1 has to be the larger genome
        if (g2.GetMaxinnovation() > g1.GetMaxinnovation()) {
            Genome temp = g1;
            g1 = g2;
            g2 = temp;
        }

        Genome gtofinddisjointsfrom;
        Genome otherg;

        // set what parents to get the disjoint genes from
        if (parent == Parent.G1) {
            gtofinddisjointsfrom = g1;
            otherg = g2;
        } else {
            otherg = g1;
            gtofinddisjointsfrom = g2;
        }

        for (Bias b : gtofinddisjointsfrom.biases) {
            if (!otherg.ContainsGene(b.innovation_number)) {
                // if a gene is within the innovationrange of the smaller genome and it doesnt exist in the other genome its a disjoint gene
                if (b.innovation_number <= g2.GetMaxinnovation()) {
                    biasgenes.add(b);
                }
            }
        }
        
        return biasgenes;
    }

    // returns all of the excess connection genes
    static ArrayList<Connection> GetExcessConnections(Genome g1, Genome g2, Parent parent) {
        ArrayList<Connection> connectiongenes = new ArrayList<Connection>();

        if (g1 == null || g2 == null) {
            return connectiongenes;
        }

        // g1 has to be the larger genome
        if (g2.GetMaxinnovation() > g1.GetMaxinnovation()) {
            Genome temp = g1;
            g1 = g2;
            g2 = temp;
        }

        Genome gtofinddisjointsfrom;
        Genome otherg;

        // set what parents to get the excess genes from
        if (parent == Parent.G1) {
            gtofinddisjointsfrom = g1;
            otherg = g2;
        } else {
            otherg = g1;
            gtofinddisjointsfrom = g2;
        }

        for (Connection c : gtofinddisjointsfrom.connections) {
            if (!otherg.ContainsGene(c.innovation_number)) {
                // if a gene is outside the innovationrange of the smaller genome and it doesnt exist in the other genome its a disjoint gene
                if (c.innovation_number > g2.GetMaxinnovation()) {
                    connectiongenes.add(c);
                }
            }
        }
        
        return connectiongenes;
    }

    // returns all of the excess bias genes
    static ArrayList<Bias> GetExcessBiases(Genome g1, Genome g2, Parent parent) {
        ArrayList<Bias> biasgenes = new ArrayList<Bias>();

        if (g1 == null || g2 == null) {
            return biasgenes;
        }

        // g1 has to be the larger genome
        if (g2.GetMaxinnovation() > g1.GetMaxinnovation()) {
            Genome temp = g1;
            g1 = g2;
            g2 = temp;
        }

        Genome gtofinddisjointsfrom;
        Genome otherg;

        // set what parents to get the excess genes from
        if (parent == Parent.G1) {
            gtofinddisjointsfrom = g1;
            otherg = g2;
        } else {
            otherg = g1;
            gtofinddisjointsfrom = g2;
        }

        for (Bias b : gtofinddisjointsfrom.biases) {
            if (!otherg.ContainsGene(b.innovation_number)) {
                // if a gene is within the innovationrange of the smaller genome and it doesnt exist in the other genome its a disjoint gene
                if (b.innovation_number > g2.GetMaxinnovation()) {
                    biasgenes.add(b);
                }
            }
        }
        
        return biasgenes;
    }

    // returns all of the matching connection genes
    static ArrayList<Connection> GetMatchingConnections(Genome g1, Genome g2, Parent parent) {
        ArrayList<Connection> matchingconns = new ArrayList<Connection>();

        if (g1 == null || g2 == null) {
            return matchingconns;
        }

        for (Connection c : g2.connections) {
            // if the gene is in both of the genomes then it is a matching gene
            if (g1.ContainsGene(c.innovation_number)) {
                // depending on what parent specified in the parameter add the matching gene
                matchingconns.add(parent == Parent.G1 ? g1.GetConnectionGene(c.innovation_number) : c);
            }
        }
        
        return matchingconns;
    }
    
    // returns all of the matching bias genes
    static ArrayList<Bias> GetMatchingBiases(Genome g1, Genome g2, Parent parent) {
        ArrayList<Bias> matchingbiases = new ArrayList<Bias>();

        if (g1 == null || g2 == null) {
            return matchingbiases;
        }

        for (Bias b : g2.biases) {
            // if the gene is in both of the genomes then it is a matching gene
            if (g1.ContainsGene(b.innovation_number)) {
                // depending on what parent specified in the parameter add the matching gene
                matchingbiases.add(parent == Parent.G1 ? g1.GetBiasGene(b.innovation_number) : b);
            }
        }
        
        return matchingbiases;
    }
    
    // returns the average weight difference
    static float GetAverageweightDifference(Genome g1, Genome g2) {
        if (g1 == null || g2 == null) {
            return 0;
        }

        float weightsum = 0;

        ArrayList<Connection> matchingconns1 = GenomeHelper.GetMatchingConnections(g1, g2, Parent.G1);
        ArrayList<Connection> matchingconns2 = GenomeHelper.GetMatchingConnections(g1, g2, Parent.G2);

        for (int i = 0; i < matchingconns1.size(); i++) {
            weightsum += Math.abs(matchingconns1.get(i).weight - matchingconns2.get(i).weight);
        }

        // if there aren't any matchingconnections then return 0, otherwise there would be a 0 division exception
        return matchingconns1.size() > 0 ? weightsum / matchingconns1.size() : 0;
    }

    // returns a childgenome based on the parents
    static Genome Crossover(Genome g1, Genome g2, float g1fitness, float g2fitness, Random r) {
        if (g1 == null || g2 == null) {
            throw new IllegalArgumentException("G1 and G2 cannot be null.");
        }

        // g1 has to be the !!!fitter!!! genome
        if (g2fitness > g1fitness) {
            Genome temp = g1;
            g1 = g2;
            g2 = temp;
        }

        Genome crossover;
        ArrayList<Node> nodes;

        if (g1.nodes.size() > g2.nodes.size()) {
            nodes = new ArrayList<Node>();
            for (Node n : g1.nodes) {
                nodes.add(n.Copy());
            }
        } else {
            nodes = new ArrayList<Node>();
            for (Node n : g2.nodes) {
                nodes.add(n.Copy());
            }
        }
        
        crossover = new Genome(nodes, g1.max_mutation_attempts);

        ArrayList<Connection> matchingconns1 = GenomeHelper.GetMatchingConnections(g1, g2, Parent.G1);
        ArrayList<Bias>  matchingbiases1 = GenomeHelper.GetMatchingBiases(g1, g2, Parent.G1);
        ArrayList<Connection> matchingconns2 = GenomeHelper.GetMatchingConnections(g1, g2, Parent.G2);
        ArrayList<Bias> matchingbiases2 = GenomeHelper.GetMatchingBiases(g1, g2, Parent.G2);

        for (int i = 0; i < matchingconns1.size(); i++) {
            crossover.AddConnection(new Connection(matchingconns1.get(i).source_node_id, matchingconns1.get(i).target_node_id, 
                r.nextFloat() > 0.5 ? matchingconns1.get(i).is_expressed : matchingconns2.get(i).is_expressed, matchingconns1.get(i).innovation_number,
                r.nextFloat() > 0.5 ? matchingconns1.get(i).weight : matchingconns2.get(i).weight));
        }

        for (int i = 0; i < matchingbiases1.size(); i++) {
            crossover.AddBias(new Bias(matchingbiases1.get(i).node, r.nextFloat() > 0.5 ? matchingbiases1.get(i).value
                : matchingbiases2.get(i).value, matchingbiases1.get(i).innovation_number));
        }

        ArrayList<Connection> disjointconns;
        ArrayList<Bias> disjointbias;
        ArrayList<Connection> excessconns;
        ArrayList<Bias> excessbias;

        disjointconns = GetDisjointConnections(g1, g2, Parent.G1);
        disjointbias = GetDisjointBiases(g1, g2, Parent.G1);
        
        excessconns = GetExcessConnections(g1, g2,Parent.G1);
        excessbias = GetExcessBiases(g1, g2, Parent.G1);

        for (Connection c : disjointconns) {
            crossover.AddConnection(new Connection(c.source_node_id, c.target_node_id, c.is_expressed, c.innovation_number, c.weight));
        }

        for (Connection c : excessconns) {
            crossover.AddConnection(new Connection(c.source_node_id, c.target_node_id, c.is_expressed, c.innovation_number, c.weight));
        }

        for (Bias b : disjointbias) {
            crossover.AddBias(new Bias(b.node, b.value, b.innovation_number));
        }

        for (Bias b : excessbias) {
            crossover.AddBias(new Bias(b.node, b.value, b.innovation_number));
        }

        return crossover;
    }
}
