package com.suti.community.java;

import java.util.Arrays;

public class BubbleSort {
    public static void main(String[] args) {
        int[] arr = {7, 2, 1, 6, 8, 5, 3, 4};
        bubbleSort(arr);
        System.out.println("Sorted array: " + Arrays.toString(arr));
    }

    public static void bubbleSort(int[] arr){
        int n = arr.length;
        for(int i=0;i<n-1;i++){
            boolean flag = false;
            for(int j=0;j<n-1-i;j++){
                if(arr[j]>arr[j+1]){
                    int tmp = arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=tmp;
                    flag=true;
                }
            }
            if(!flag)
                break;
        }
    }
















    //public static void bubbleSort(int[] arr) {
    //    int n = arr.length;
    //    // 外层循环控制遍历次数
    //    for (int i = 0; i < n - 1; i++) {
    //        boolean swapped = false; // 标志位，用于优化冒泡排序
    //        // 内层循环进行相邻元素比较和交换
    //        for (int j = 0; j < n - 1 - i; j++) {
    //            if (arr[j] > arr[j + 1]) {
    //                // 如果相邻元素逆序，则交换它们的位置
    //                int temp = arr[j];
    //                arr[j] = arr[j + 1];
    //                arr[j + 1] = temp;
    //                swapped = true;
    //            }
    //        }
    //        // 如果本轮遍历没有发生交换，则数组已经有序，提前结束排序
    //        if (!swapped) {
    //            break;
    //        }
    //    }
    //}
}
