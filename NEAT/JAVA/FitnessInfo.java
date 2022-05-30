class FitnessInfo {
    Genome Genome;
    float Fitness;
    float SharedFitness;

    FitnessInfo(Genome genome, float fitness, float sharedfitness) {
        this.Genome = genome;
        this.Fitness = fitness;
        this.SharedFitness = sharedfitness;
    }
}
