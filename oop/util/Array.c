/* Create a singly list with dummy head node */
#include "Array.h"
#include <stdio.h>
#include <string.h>


proposition Array_isEmpty(Array* array){
	return array->num == 0;
}

/* Dispose the list */
void Array_destroy(Array* array) {
	array->maxSize = -1;
	free(array->array);
	free(array);
}


int Array_maxSize(Array* array) {
	return array->maxSize;
}


Array* Array_new(int maxSize, BStar_Equal elemEqual) {

	Array* array = NULL;

	if (maxSize <= 0) {
		return NULL;
	}

	if ((array = (Array *)malloc(sizeof(Array))) == NULL) {
		fprintf(stderr, "Out of memory!\n");
		exit(1);
	}

	if ((array->array = (BStar_Object *)malloc(sizeof(BStar_Object) * maxSize)) == NULL) {
		fprintf(stderr, "Out of memory!\n");
		exit(1);
	}
	// memset(array->array, 0, sizeof(BStar_Object) * maxSize);
	array->maxSize = maxSize;
	array->num = 0;
	array->equal = elemEqual;

	return array;
}

BStar_Object Array_get(Array* array, BStar_Object e) {
	return List_get2(array, e, array->equal);
}

BStar_Object Array_get2(Array* array, BStar_Object e, BStar_Equal equal) {
	int i = 0;
	while(i < array->num) {
		if(equal(array->array[i], e)) {
			return array->array[i];
		} else {
			i++;
		}
	}
	return NULL;
}

BStar_Object Array_get_poi(Array* array, int poi) {
	if(poi >= array->num || poi < 0) {
		return NULL;
	} else {
		return array->array[poi];
	}
}

proposition Array_delete(Array* array, BStar_Object e) {
	return Array_delete2(array, e, array->elemEqual);
}

/* Delete the first node in List list when x is found */
proposition Array_delete2(Array* array, BStar_Object e, BStar_Equal equal) {
	int i = 0;
	while(i < array->num) {
		if(equal(array->array[i], e)) {
			while(i+1<array->num) {
				array->array[i] = array->array[i+1];
				i++;
			}
			array->num--;
			return true;
		} else {
			i++;
		}
	}
	return false;
}

void Array_add(Array* array, BStar_Object e) {
	if(!Array_get(array, e) == NULL) {
		array->array[array->num++] = e;
	}
}

Array* Array_add_Array(Array* array, Array* array2) {
	int i = array->num;
	int j = 0;
	while(j < array2->num) {
		if(Array_get(array, array2->array[j]) == NULL) {
			if(i+1>array->maxSize) {
				return NULL;
			} else {
				array->array[i++] = array2[j++];
			}
		}
	}
	return array;
}

Array* Array_delete_Array(Array* array, Array* array2) {
	int i = 0;
	while(i < array2->num) {
		Array_delete(array, array2->array[i]);
	}
	return array;
}
