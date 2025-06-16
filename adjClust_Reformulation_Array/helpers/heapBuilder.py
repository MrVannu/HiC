import heapq

def build_min_heap(input_list):
    values = [float(x) for x in input_list.split(',')] 
    
    # Convert the list into a min-heap
    heapq.heapify(values)
    return values

def print_tree(heap):
    n = len(heap)
    level = 0
    max_width = 2 ** (len(bin(n)) - 2) * 2  # calculate tree width based on number of nodes

    while (2 ** level - 1) < n:
        start_idx = 2 ** level - 1
        end_idx = min(2 ** (level + 1) - 1, n)
        current_level = heap[start_idx:end_idx]
        
        indent_space = ' ' * (max_width // (2 ** (level + 1)))
        level_string = indent_space.join(map(str, current_level))
        
        print(level_string.center(max_width))
        level += 1

if __name__ == "__main__":
    input_list = input("Enter a list of values separated by commas: ")
    min_heap = build_min_heap(input_list)
    
    print("\nMin Heap as a Binary Tree:")
    print_tree(min_heap)
