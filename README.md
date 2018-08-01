## Dual Thread Example in Java

A quick DEMO for Java's Thread API:

1. Using `Executors` for the Thread control.
2. Create two process that must wait until each other's action was done

## Expected Output of Execution

The `CarCarer` should wait until the `CarStatus` is able to do the assign action.

In simplest condition, the washer and waxer will wait each other as below:

```shell
Washing    start .... finish!
Waxing     start .... finish!
Washing    start .... finish!
Waxing     start .... finish!
Washing    start .... finish!
Waxing     start .... finish!
Washing    start .... finish!
Waxing     start .... finish!
Washing    start .... finish!
Waxing     start .... finish!
Washing    start .... finish!
=== Program Finished ==
```
