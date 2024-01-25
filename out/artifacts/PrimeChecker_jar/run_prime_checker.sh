#!/bin/bash

# Clear the screen
clear

# Check if command-line arguments were provided
if [ $# -ge 2 ]; then
  upper_bound=$1
  num_threads=$2
else
  # Prompt the user for the upper bound and the number of threads
  read -p "Enter the upper bound of integers to check: " upper_bound
  read -p "Enter the number of threads to use: " num_threads
fi

# Check for empty inputs and assign default values if necessary
if [ -z "$upper_bound" ]; then
  echo "No upper bound entered. Please provide an upper bound."
  exit 1
fi

if [ -z "$num_threads" ]; then
  echo "No number of threads entered. Using default thread count."
  num_threads="1" # Or you can assign to DEFAULT_THREAD_COUNT if it is defined
fi

# Run the Java program for cache runs
echo " --- RUNNING UP TO $upper_bound, USING $num_threads THREADS --- "
for (( i=1; i<=3; i++ )); do
  echo "-----------------------------------------------"
  echo "|               Cache run $i                   |"
  echo "-----------------------------------------------"
  echo -e "${upper_bound}\n${num_threads}" | java -jar PrimeChecker.jar
done

# Delineation between cache runs and timed runs
echo ""
echo " --- CACHING DONE --- "

# Run the Java program for timed runs
for (( i=1; i<=5; i++ )); do
  echo "-----------------------------------------------"
  echo "|               Timed run $i                   |"
  echo "-----------------------------------------------"
  echo -e "${upper_bound}\n${num_threads}" | java -jar PrimeChecker.jar
done

# Inform the user that all runs have been completed
echo ""
echo " --- ALL RUNS HAVE BEEN COMPLETED. --- "
