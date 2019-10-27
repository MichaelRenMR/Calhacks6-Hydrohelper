float average(int myList[], int size) {
  float sum = 0.0;
  for (int i = 0; i < size; i++) {
    sum += myList[i];
  }
  return sum / size; 
}




float calculateSD(int data[], int size)
{
    float sum = 0.0, mean, standardDeviation = 0.0;
    int i;
    mean = average(data, size);
    for(i = 0; i < size; ++i)
        standardDeviation += pow(data[i] - mean, 2);
    return sqrt(standardDeviation / size);
}
