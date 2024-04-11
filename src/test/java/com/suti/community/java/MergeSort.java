package com.suti.community.java;

import java.util.Arrays;

public class MergeSort {
    public static void main(String[] args) {
        int[] arr = {7, 2, 1, 6, 8, 5, 3, 4};
        mergeSort(arr, 0, arr.length - 1);
        System.out.println("Sorted array: " + Arrays.toString(arr));
    }

    public static void mergeSort(int[] arr,int left,int right){
        if(left<right){
            int mid = left+(right-left)/2;
            mergeSort(arr,left,mid);
            mergeSort(arr,mid+1,right);
            merge(arr,left,mid,right);
        }
    }

    public static void merge(int[] arr,int left,int mid,int right){
        int[] tmp = new int[right-left+1];
        int i = left;
        int j = mid+1;
        int k=0;
        while(i<=mid && j<=right){
            if(arr[i]<=arr[j]){
                tmp[k++]=arr[i++];
            }else{
                tmp[k++]=arr[j++];
            }
        }
        while(i<=mid){
            tmp[k++]=arr[i++];
        }
        while(j<=right){
            tmp[k++]=arr[j++];
        }

        for(int m=left;m<=right;m++){
            arr[m]=tmp[m-left];
        }
    }














    //public static void mergeSort(int[] arr, int left, int right) {
    //    if (left < right) {
    //        int mid = left + (right - left) / 2; // 计算中间位置
    //        mergeSort(arr, left, mid); // 对左半部分进行归并排序
    //        mergeSort(arr, mid + 1, right); // 对右半部分进行归并排序
    //        merge(arr, left, mid, right); // 合并两个有序子数组
    //    }
    //}
    //
    //public static void merge(int[] arr, int left, int mid, int right) {
    //    int[] temp = new int[right - left + 1]; // 创建临时数组存放合并后的结果
    //    int i = left; // 左子数组的起始索引
    //    int j = mid + 1; // 右子数组的起始索引
    //    int k = 0; // 临时数组的索引
    //    // 合并两个有序子数组
    //    while (i <= mid && j <= right) {
    //        if (arr[i] <= arr[j]) {
    //            temp[k++] = arr[i++];
    //        } else {
    //            temp[k++] = arr[j++];
    //        }
    //    }
    //    // 将剩余元素拷贝到临时数组中
    //    while (i <= mid) {
    //        temp[k++] = arr[i++];
    //    }
    //    while (j <= right) {
    //        temp[k++] = arr[j++];
    //    }
    //    // 将临时数组的元素拷贝回原始数组中
    //    for (int m = 0; m < temp.length; m++) {
    //        arr[left + m] = temp[m];
    //    }
    //}
}
