import java.util.ArrayList;
import java.util.Random;

class Species
{
    int staleness;

    Genome Representative;

    float bestfitness;

    ArrayList<FitnessInfo> genomes;

    float GetTotalSharedFitness() {
        float totalfitness = 0;
        for (FitnessInfo fi : this.genomes) {
            totalfitness += fi.SharedFitness;
        }

        return totalfitness;
    }

    FitnessInfo GetFittestGenome() {
        if (this.genomes.size() > 0) {
            FitnessInfo fittest = this.genomes.get(0);

            for (FitnessInfo fg : this.genomes) {
                if (fg.Fitness > fittest.Fitness) {
                    fittest = fg;
                }
            }

            return fittest;
        } else {
            return null;
        }
    }

    Species(Genome representative) {
        this(representative, new ArrayList<FitnessInfo>());
    }

    void AddGenome(Genome genometoadd) {
        if (genometoadd != null) {
            for (FitnessInfo fg : this.genomes) {
                if (fg.Genome == genometoadd) {
                    return;
                }
            }

            this.genomes.add(new FitnessInfo(genometoadd, 0.0f, 0.0f));
        }
    }

    Genome CreateOffspring(Random r) {
        FitnessInfo[] parents = new FitnessInfo[2];
        int genomecount = this.genomes.size();
        for (int i = 0; i < parents.length; i++) {
            float randomfitness = r.nextFloat() * this.GetTotalSharedFitness();
            for (FitnessInfo fg : this.genomes) {
                randomfitness -= fg.SharedFitness;
                if (randomfitness <= 0 || fg == this.genomes.get(genomecount -1 )) {
                    parents[i] = fg;
                    break;
                }
            }
        }

        return GenomeHelper.Crossover(parents[0].Genome, parents[1].Genome, parents[0].Fitness, parents[1].Fitness, r);
    } 

    Species(Genome representative, ArrayList<FitnessInfo> genomes) {
        this.Representative = representative;
        if (genomes != null) {
            this.genomes = genomes;
            for (FitnessInfo fg : this.genomes) {
                if (fg.Genome == representative) {
                    return;
                }
            }

            this.genomes.add(new FitnessInfo(representative, 0.0f, 0.0f));
        } else {
            this.genomes = new ArrayList<FitnessInfo>();
        }
    }

    void KillOffWorst() {
        for (int i = 0; i < this.genomes.size(); ++i) {
            for (int j = i + 1; j < this.genomes.size(); ++j) {
                if (this.genomes.get(i).Fitness > this.genomes.get(j).Fitness) {
                    FitnessInfo tmp = this.genomes.get(i);
                    this.genomes.set(i, this.genomes.get(j));
                    this.genomes.set(j, tmp);
                }
            }
        }

        while (this.genomes.size() > 5) {
            this.genomes.remove(0);
        }
    }

    void CheckStaleness() {
        if (this.GetTotalSharedFitness()  <= this.bestfitness) {
            this.staleness++;
        } else {
            this.bestfitness = this.GetTotalSharedFitness();
            if (staleness != 0) {
                staleness = 0;
            }
        }
    }

    Species Copy() {
        Species copy = new Species(this.Representative.Copy());
        for (FitnessInfo fg : this.genomes) {
            copy.AddGenome(fg.Genome.Copy());
        }
        
        return copy;
    }

    void Clear() {
        this.genomes.clear();
    }
}
