package com.suti.community.java;

import java.util.Arrays;

public class HeapSort {
    public static void main(String[] args) {
        int[] arr = {7, 2, 1, 6, 8, 5, 3, 4};
        heapSort(arr);
        System.out.println("Sorted array: " + Arrays.toString(arr));
    }

    public static void heapSort(int[] arr){
        int n = arr.length;
        for(int i=n/2-1;i>0;i--){
            heapify(arr,n,i);
        }
        for(int i=n-1;i>0;i--){
            swap(arr,0,i);
            heapify(arr,i,0);
        }
    }

    public static void heapify(int[] arr,int size,int i){
        int largest = i;
        int left = 2*i+1;
        int right = 2*i+2;
        if(left<size && arr[left]>arr[largest]){
            largest=left;
        }
        if(right<size && arr[right]>arr[largest]){
            largest=right;
        }
        if(largest!=i){
            swap(arr,i,largest);
            heapify(arr,size,largest);
        }
    }

    public static void swap(int[] arr,int i,int j){
        int tmp = arr[i];
        arr[i]=arr[j];
        arr[j]=tmp;
    }










    //public static void heapSort(int[] arr) {
    //    int n = arr.length;
    //
    //    // 构建大顶堆
    //    for (int i = n / 2 - 1; i >= 0; i--) {
    //        heapify(arr, n, i);
    //    }
    //
    //    // 依次取出堆顶元素并调整堆结构
    //    for (int i = n - 1; i > 0; i--) {
    //        swap(arr, 0, i); // 将堆顶元素（最大值）与当前未排序部分的最后一个元素交换
    //        heapify(arr, i, 0); // 调整堆结构，重新构建大顶堆
    //    }
    //}
    //
    //public static void heapify(int[] arr, int n, int i) {
    //    int largest = i; // 假设当前节点为最大值
    //    int left = 2 * i + 1; // 左子节点索引
    //    int right = 2 * i + 2; // 右子节点索引
    //
    //    // 比较左子节点和根节点的大小
    //    if (left < n && arr[left] > arr[largest]) {
    //        largest = left;
    //    }
    //
    //    // 比较右子节点和最大值的大小
    //    if (right < n && arr[right] > arr[largest]) {
    //        largest = right;
    //    }
    //
    //    // 如果最大值不是根节点，则交换节点并继续向下调整堆结构
    //    if (largest != i) {
    //        swap(arr, i, largest);
    //        heapify(arr, n, largest);
    //    }
    //}
    //
    //public static void swap(int[] arr, int i, int j) {
    //    int temp = arr[i];
    //    arr[i] = arr[j];
    //    arr[j] = temp;
    //}
}
