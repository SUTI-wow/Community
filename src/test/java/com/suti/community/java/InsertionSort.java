package com.suti.community.java;

import java.util.Arrays;

public class InsertionSort {
    public static void main(String[] args) {
        int[] arr = {7, 2, 1, 6, 8, 5, 3, 4};
        insertionSort(arr);
        System.out.println("Sorted array: " + Arrays.toString(arr));
    }

    public static void insertionSort(int[] arr){
        int n = arr.length;
        for(int i=1;i<n;i++){
            int key = arr[i];
            int j = i-1;
            while(j>=0 && arr[j]>key){
                arr[j+1]=arr[j];
                j--;
            }
            arr[j+1]=key;
        }
    }














    //public static void insertionSort(int[] arr) {
    //    int n = arr.length;
    //    // 外层循环从第二个元素开始，依次将元素插入到已排序部分
    //    for (int i = 1; i < n; i++) {
    //        int key = arr[i]; // 当前待插入的元素值
    //        int j = i - 1; // 已排序部分的最后一个元素的索引
    //        // 内层循环将当前元素插入到已排序部分的合适位置
    //        while (j >= 0 && arr[j] > key) {
    //            arr[j + 1] = arr[j]; // 向后移动大于当前元素值的元素
    //            j--;
    //        }
    //        arr[j + 1] = key; // 将当前元素插入到合适位置
    //    }
    //}
}