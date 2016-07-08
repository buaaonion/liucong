#ifndef OOP_UTIL_Array_H_INCLUDED_
#define OOP_UTIL_Array_H_INCLUDED_
#include "BStar.h"

typedef struct Array {
	BStar_Object *array;
	int maxSize;
	int num;
	BStar_Equal equal;
} Array;

extern proposition Array_isEmpty(Array* array);

/* Dispose the list */
extern void Array_destroy(Array* array) ;

extern int Array_maxSize(Array* array) ;


extern Array* Array_new(int maxSize, BStar_Equal elemEqual) ;

extern BStar_Object Array_get(Array* array, BStar_Object e) ;

extern BStar_Object Array_get2(Array* array, BStar_Object e, BStar_Equal equal) ;

extern BStar_Object Array_get_poi(Array* array, int poi) ;

extern proposition Array_delete(Array* array, BStar_Object e) ;

/* Delete the first node in List list when x is found */
extern proposition Array_delete2(Array* array, BStar_Object e, BStar_Equal equal) ;

extern void Array_add(Array* array, BStar_Object e) ;

extern Array* Array_add_Array(Array* array, Array* array2);

extern Array* Array_delete_Array(Array* array, Array* array2);

#endif