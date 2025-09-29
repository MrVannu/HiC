import random
from deap import base, creator, tools, algorithms
import csv
 
RANDOM_SEED = 121
random.seed(RANDOM_SEED)

POP_SIZE = 200
MUT_PROB = 0.2
TOURN_SIZE = 3
NUM_GEN = 80
CROSS_PROB = 0.7

INPUT_FILE = "ld_data/outputs/BASE_ld_upper.tsv"
OUTPUT_ORDER_FILE = "ld_data/outputs/best_order_upper_genAlg.tsv"
OUTPUT_LONG_FILE = "ld_data/outputs/sorted_genAlg_upper_matrix.tsv"
 
similarity_dict = {}
nodes_set = set()

with open(INPUT_FILE) as f:
    reader = csv.DictReader(f, delimiter="\t")
    for row in reader:
        try:
            a = int(row["BP_A"])
            b = int(row["BP_B"])
            r2 = float(row["R2"])
        except (ValueError, KeyError):
            continue
        similarity_dict[(a,b)] = r2
        similarity_dict[(b,a)] = r2  # simmetric
        nodes_set.update([a,b])

node_identifiers = sorted(nodes_set)
number_of_nodes = len(node_identifiers)
print(f"Total unique nodes: {number_of_nodes}")


# Create index mappings
node_to_idx = {node: i for i, node in enumerate(node_identifiers)}
idx_to_node = {i: node for node, i in node_to_idx.items()}
all_indices = list(range(number_of_nodes))  # used in genAlg

# Create fitness
creator.create("FitnessMax", base.Fitness, weights=(1.0,))
creator.create("Individual", list, fitness=creator.FitnessMax)

def fitness_function(ind):
    """Somma R2 tra nodi adiacenti nella permutazione"""
    total_similarity = 0.0
    for i in range(len(ind)-1):
        a = idx_to_node[ind[i]]
        b = idx_to_node[ind[i+1]]
        total_similarity += similarity_dict.get((a,b), 0.0)
    return (total_similarity,)

# Setup DEAP toolbox
toolbox = base.Toolbox()
toolbox.register("individual", tools.initIterate, creator.Individual,
                 lambda: random.sample(all_indices, number_of_nodes))
toolbox.register("population", tools.initRepeat, list, toolbox.individual)
toolbox.register("evaluate", fitness_function)
toolbox.register("mate", tools.cxOrdered)
toolbox.register("mutate", tools.mutShuffleIndexes, indpb=MUT_PROB)
toolbox.register("select", tools.selTournament, tournsize=TOURN_SIZE)

# Execute genetic algorithm
population = toolbox.population(n=POP_SIZE)
hall_of_fame = tools.HallOfFame(1)

for gen in range(NUM_GEN):
    offspring = algorithms.varAnd(population, toolbox,
                                  cxpb=CROSS_PROB,
                                  mutpb=MUT_PROB)
    fits = list(map(toolbox.evaluate, offspring))
    for fit, ind in zip(fits, offspring):
        ind.fitness.values = fit
    population = toolbox.select(offspring, k=len(population))
    hall_of_fame.update(population)
    print(f"Generation {gen+1}, best fitness: {hall_of_fame[0].fitness.values[0]:.6f}")

best_order = hall_of_fame[0]

# Recover original node identifiers
best_order_nodes = [idx_to_node[i] for i in best_order]

# Besr order output
with open(OUTPUT_ORDER_FILE, "w") as f:
    for node in best_order_nodes:
        f.write(f"{node}\n")

# Sorted long-format upper matrix output
with open(OUTPUT_LONG_FILE, "w") as f:
    f.write("BP_A\tBP_B\tR2\n")
    for i, bp_a in enumerate(best_order_nodes):
        for j in range(i+1, number_of_nodes):  # upper matrix only
            bp_b = best_order_nodes[j]
            r2 = similarity_dict.get((bp_a, bp_b), 0.0)
            f.write(f"{bp_a}\t{bp_b}\t{r2:.6f}\n")

print(f"Best order saved to {OUTPUT_ORDER_FILE}")
print(f"Sorted long-format upper matrix saved to {OUTPUT_LONG_FILE}")
