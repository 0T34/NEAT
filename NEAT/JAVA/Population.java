import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

enum Chance {
    AddNode, AddConnection, EnableDisableConenction, AdjustBias, AddBias, Adjustweight, Mutate
}

class Population {
    HashMap<Integer, FitnessInfo> FittestGenome;
    
    HashMap<Integer, ArrayList<Genome>> fittestgenomesingeneration;
  
    ArrayList<Species> species;
  
    int currentgeneration;
  
    int currentspecies;
  
    int currentgenome;
  
    int populationsize;
    
    void ResetGenomePointer() {
        this.currentspecies = 0;
        this.currentgenome = 0;
    }
  
    HashMap<Chance, Float> chances;
  
    int inputnodes;
  
    int outputnodes;
    
    int max_mutation_attempts;
    
    float threshold;
  
    float excessimportance;
  
    float disjointimportance;
    
    float deltaweightimportance;
  
    Genome startgenome;
  
    InnovationMachine innovationmachine;
  
    Population(int populationsize, int inputnodes, int outputnodes, int max_mutation_attempts, float threshold, float excessimportance, 
               float disjointimportance, float averageweightdiffimportance, HashMap<Chance, Float> chances, Random r) {
        SetupChances(chances);
        this.max_mutation_attempts = startgenome.max_mutation_attempts;
        this.populationsize = populationsize;
        this.inputnodes = startgenome.inputs;
        this.outputnodes = startgenome.outputs;
        this.threshold = threshold;
        this.deltaweightimportance = averageweightdiffimportance;
        this.disjointimportance = disjointimportance;
        this.excessimportance = excessimportance;
        this.startgenome = new Genome(inputnodes, outputnodes, max_mutation_attempts);
        this.innovationmachine = new InnovationMachine(this.startgenome.GetMaxinnovation() + 1);
        ResetPopulation(r);
    }

    Population(int populationsize, Genome startgenome, float threshold, float excessimportance, float disjointimportance,
               float averageweightdiffimportance, HashMap<Chance, Float> chances, Random r) {
        SetupChances(chances);
        this.max_mutation_attempts = startgenome.max_mutation_attempts;
        this.populationsize = populationsize;
        this.inputnodes = startgenome.inputs;
        this.outputnodes = startgenome.outputs;
        this.threshold = threshold;
        this.deltaweightimportance = averageweightdiffimportance;
        this.disjointimportance = disjointimportance;
        this.excessimportance = excessimportance;
        this.startgenome = startgenome;
        this.innovationmachine = new InnovationMachine(this.startgenome.GetMaxinnovation() + 1);
        ResetPopulation(r);
    }
  
    void SetupChances(HashMap<Chance, Float> chances) {
        // Set up the default chances
        this.chances = new HashMap<Chance, Float>();
        this.chances.put(Chance.Mutate, 1f);
        this.chances.put(Chance.AddBias, 0.05f);
        this.chances.put(Chance.AddConnection, 0.05f);
        this.chances.put(Chance.AddNode, 0.03f);
        this.chances.put(Chance.AdjustBias, 0.8f);
        this.chances.put(Chance.Adjustweight, 0.8f);
        this.chances.put(Chance.EnableDisableConenction, 0.0f);
  
        // If other chances are specified override the default ones
        if (chances != null) {
            Object[] keys = chances.keySet().toArray();
            for (int i = 0; i < chances.size(); i++) {
                Chance _key = (Chance) keys[i];
                this.chances.put(_key, chances.get(_key));
            }
        }
    }
    
    void ResetPopulation(Random r) {
        this.innovationmachine.Reset();
        for (Connection c : this.startgenome.connections) {
            this.innovationmachine.AddConnection(c);
        }

        for (Bias b : this.startgenome.biases) {
            this.innovationmachine.AddBias(b);
        }

        this.species = new ArrayList<Species>();
        this.currentgeneration = 1;
        ResetGenomePointer();
  
        this.fittestgenomesingeneration = new HashMap<Integer, ArrayList<Genome>>();
        this.FittestGenome = new HashMap<Integer, FitnessInfo>();
        ArrayList<Genome> population = new ArrayList<Genome>();
        while (population.size() < this.populationsize) {
            Genome genometoadd = startgenome.Copy();
            genometoadd.AddConnectionMutation(this.innovationmachine, r);
            population.add(genometoadd);
        }

        this.Speciate(population, r);
    }
  
    int SharingFunction(float i) {
        if (i >= this.threshold) {
            return 1;
        } else {
            return 0;
        }
    }
  
    void SetFitness(Genome g, float fitness) {
        for (Species s : this.species) {
            for (FitnessInfo fi : s.genomes) {
                if (fi.Genome == g) {
                    fi.Fitness = fitness;
                    fi.SharedFitness = fitness / s.genomes.size();
                    return;
                }
            }
        }
    }
  
    Genome NextGenome() {
        Genome nextgenome = this.species.get(this.currentspecies).genomes.get(this.currentgenome).Genome;
        this.currentgenome++;
        if (this.species.get(this.currentspecies).genomes.size() <= this.currentgenome) {
            this.currentgenome = 0;
            this.currentspecies++;
            this.currentspecies %= this.species.size();
        }

        return nextgenome;
    }
  
    void NextGeneration(Random r) {
        ArrayList<Genome> population = new ArrayList<Genome>();
        float totalfitness = 0;
        this.fittestgenomesingeneration.put(this.currentgeneration, new ArrayList<Genome>());
  
        for (int i = 0; i < this.species.size(); i++) {
            this.species.get(i).CheckStaleness();
            if (this.species.get(i).staleness >= 15) {
                if (this.species.size() > 1) {
                    this.species.remove(i--);
                    continue;
                }
            }

            this.species.get(i).KillOffWorst();
            totalfitness += this.species.get(i).GetTotalSharedFitness();
            FitnessInfo fittestgenomeinfo = this.species.get(i).GetFittestGenome();
            if (this.FittestGenome.get(this.currentgeneration) == null || this.FittestGenome.get(this.currentgeneration).Fitness < fittestgenomeinfo.Fitness) {
                this.FittestGenome.put(this.currentgeneration, fittestgenomeinfo);
            }

            this.fittestgenomesingeneration.get(this.currentgeneration).add(fittestgenomeinfo.Genome.Copy());
            population.add(fittestgenomeinfo.Genome.Copy());
        }

        this.currentgeneration++;
        while (population.size() < this.populationsize) {
            Genome offspring = this.CreateOffspring(r.nextFloat() * totalfitness, r);
            this.Mutate(offspring, r);
            population.add(offspring);
        }

        Speciate(population, r);
    }
  
    void Mutate(Genome g, Random r) {
        if (r.nextFloat() <= this.chances.get(Chance.Mutate)) {
            if (r.nextFloat() <= this.chances.get(Chance.AddBias)) {
                g.AddBiasMutation(this.innovationmachine, r);
            }

            if (r.nextFloat() <= this.chances.get(Chance.AddConnection)) {
                g.AddConnectionMutation(this.innovationmachine, r);
            }

            if (r.nextFloat() <= this.chances.get(Chance.AddNode)) {
                g.AddNodeMutation(this.innovationmachine, r);
            }

            if (r.nextFloat() <= this.chances.get(Chance.AdjustBias)) {
                g.AdjustBiasMutation(r);
            }

            if (r.nextFloat() <= this.chances.get(Chance.Adjustweight)) {
                g.AdjustweightMutation(r);
            }

            if (r.nextFloat() <= this.chances.get(Chance.EnableDisableConenction)) {
                g.EnableDisableConnectionMutation(r);
            }
        }
    }
  
    Genome CreateOffspring(float fitness, Random r) {
        Genome offspring = null;
        int speciescount = this.species.size();
        for (Species s : this.species) {
            if (fitness - s.GetTotalSharedFitness() <= 0 || s == this.species.get(speciescount - 1)) {
                offspring = s.CreateOffspring(r);
                return offspring;
            } else {
                fitness -= s.GetTotalSharedFitness();
            }
        }
        
        return offspring;
    }
  
    void Speciate(ArrayList<Genome> population, Random r) {
        for (Species s : this.species) {
            s.Representative = s.genomes.get(Math.round(r.nextFloat() * (s.genomes.size() - 1))).Genome;
            s.Clear();
        }
  
        for (Genome g : population) {
            boolean wasadded = false;
            for (Species s : this.species) {
                if (GenomeHelper.GetCompatibilityDistance(s.Representative, g, this.excessimportance, this.disjointimportance, this.deltaweightimportance) <= this.threshold) {
                    s.AddGenome(g);
                    wasadded = true;
                    break;
                }
            }
  
            if (!wasadded) {
                this.species.add(new Species(g));
            }
        }
  
        for (int i = 0; i < this.species.size(); i++) {
            if (this.species.get(i).genomes.size() == 0) {
                this.species.remove(i--);
            }
        }
    }
}
