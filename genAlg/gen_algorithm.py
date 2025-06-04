
import numpy as np
import random
from deap import base, creator, tools, algorithms
import matplotlib.pyplot as plt

INITIAL_POPULATION_SIZE = 200
MUTATION_PROBABILITY = 0.2
TOURNAMENT_SIZE = 3
NUMBER_OF_GENERATIONS = 80
CROSSOVER_PROBABILITY = 0.7

similarity_matrix = np.loadtxt("similarity_matrix.csv", delimiter=",")
number_of_nodes = similarity_matrix.shape[0]
node_identifiers = list(range(number_of_nodes))

creator.create("FitnessMax", base.Fitness, weights=(1.0,))
creator.create("Individual", list, fitness=creator.FitnessMax)

def fitness_function(individual):
    total_similarity = 0.0
    for i in range(len(individual) - 1):
        total_similarity += similarity_matrix[individual[i]][individual[i + 1]]
    return (total_similarity,)

toolbox = base.Toolbox()
toolbox.register("individual", tools.initIterate, creator.Individual, lambda: random.sample(node_identifiers, len(node_identifiers)))
toolbox.register("population", tools.initRepeat, list, toolbox.individual)
toolbox.register("evaluate", fitness_function)
toolbox.register("mate", tools.cxOrdered)
toolbox.register("mutate", tools.mutShuffleIndexes, indpb=MUTATION_PROBABILITY)
toolbox.register("select", tools.selTournament, tournsize=TOURNAMENT_SIZE)

population = toolbox.population(n=INITIAL_POPULATION_SIZE)
hall_of_fame = tools.HallOfFame(1)
for generation in range(NUMBER_OF_GENERATIONS):
    offspring = algorithms.varAnd(population, toolbox, cxpb=CROSSOVER_PROBABILITY, mutpb=MUTATION_PROBABILITY)
    fits = toolbox.map(toolbox.evaluate, offspring)
    for fit, ind in zip(fits, offspring):
        ind.fitness.values = fit
    population = toolbox.select(offspring, k=len(population))
    hall_of_fame.update(population)
    print(".", end="", flush=True)
    print(f" Generation {generation+1} best fitness: {hall_of_fame[0].fitness.values[0]:.4f}")
print()

best_order = hall_of_fame[0]
sorted_matrix = similarity_matrix[np.ix_(best_order, best_order)]

np.savetxt("sorted.csv", best_order, fmt="%d", delimiter=",")


