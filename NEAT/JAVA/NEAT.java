import java.util.HashMap;
import java.util.Random;

class InputTarget
{
  public float[] input;

  public float target;

  public InputTarget(float[] input, float target)
  {
    this.input = input;

    this.target = target;
  }
}

class NEAT {
  	public static void main(String args[]) {
		Random r = new Random();
		NetworkPrinter np;

		Genome start = new Genome(2, 1, 5);
		HashMap<Chance, Float> mutationrates = new HashMap<Chance, Float>();
		mutationrates.put(Chance.AddConnection, 0.45f);
		Population p = new Population(150, start, 3f, 1f, 1f, 0.4f, mutationrates, r);
		Network currentnet = null;
		InputTarget[] it =
		{
		  new InputTarget(new float[] {1, 0}, 1), 
		  new InputTarget(new float[] {0, 1}, 1), 
		  new InputTarget(new float[] {1, 1}, 0), 
		  new InputTarget(new float[] {0, 0}, 0),
		};
		
		boolean solutionfound = false;
		
		do
		{
		  for (int j = 0; j < p.GetPopulationsize(); j++)
		  {
			Genome currentgenome = p.NextGenome();
			currentnet = new Network(currentgenome);
			float fitness = 0f;
			for (InputTarget currentit : it)
			{
			  fitness += Math.abs(currentit.target - currentnet.FeedForward(currentit.input)[0]);
			}
			fitness = 4 - fitness;
			p.SetFitness(currentgenome, (float)Math.pow(fitness, 2));
			
			if(fitness > 3.6f)
			{
			  solutionfound = true;
			}
		  }
		  
		  p.NextGeneration(r);
		} while (!solutionfound);
		
		System.out.println("done in: " + p.GetCurrentgeneration() + " generations");
		
		Genome winnergenome = p.FittestGenome.get(p.GetCurrentgeneration()-1).Genome;
		currentnet = new Network(winnergenome);
		
		for (InputTarget currentit : it)
		{
		  System.out.println("inputs: " + currentit.input[0] + currentit.input[1] +
		  					 " expected result: " + currentit.target + currentnet.FeedForward(currentit.input)[0]);
		}
		
		np = new NetworkPrinter(winnergenome);
		np.Print();
	}
}
