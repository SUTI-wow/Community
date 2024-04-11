package com.suti.community.java;

import java.util.Arrays;

public class SelectSort {
    public static void main(String[] args) {
        int[] arr = {7, 2, 1, 6, 8, 5, 3, 4};
        selectSort(arr);
        System.out.println("Sorted array: " + Arrays.toString(arr));
    }

    public static void selectSort(int[] arr){
        int n = arr.length;
        for(int i=0;i<n-1;i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                int tmp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = tmp;
            }
        }
    }













    //public static void selectionSort(int[] arr) {
    //    int n = arr.length;
    //    // 外层循环控制遍历次数
    //    for (int i = 0; i < n - 1; i++) {
    //        int minIndex = i; // 假设当前位置为最小值的索引
    //        // 内层循环寻找最小值的索引
    //        for (int j = i + 1; j < n; j++) {
    //            if (arr[j] < arr[minIndex]) {
    //                minIndex = j;
    //            }
    //        }
    //        // 将最小值与当前位置交换
    //        if (minIndex != i) {
    //            int temp = arr[i];
    //            arr[i] = arr[minIndex];
    //            arr[minIndex] = temp;
    //        }
    //    }
    //}
}
