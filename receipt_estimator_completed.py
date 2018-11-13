from __future__ import print_function

import argparse
import pandas as pd
import tensorflow as tf
import json

CSV_COLUMN_NAMES = ['isNumeric', 'isTotal', 'xLeftRel', 'yTopRel', 'category']

def train_input_fn():
    """An input function for training. It loads data from a CSV file and returns a Tensorflow Dataset"""

    #CSV filename, array of the column names, which row has the headers.
    train = pd.read_csv("Training_Dataset.csv", names=CSV_COLUMN_NAMES, header=0)
    train_features, train_label = train, train.pop("category")

    # Convert the inputs to a Dataset.
    dataset = tf.data.Dataset.from_tensor_slices((dict(train_features), train_label))

    # Shuffle, repeat, and batch the examples.
    dataset = dataset.shuffle(1000).repeat().batch(BATCH_SIZE)

    # Return the dataset.
    return dataset

def eval_input_fn():
    """An input function for testing. It loads data from a CSV file and returns a Tensorflow Dataset"""

    #CSV filename, array of the column names, which row has the headers.
    test = pd.read_csv("Test_Dataset.csv", names=CSV_COLUMN_NAMES, header=0)
    test_features, test_label = test, test.pop("category")

    # Convert the inputs to a Dataset.
    dataset = tf.data.Dataset.from_tensor_slices((dict(test_features), test_label))
    dataset = dataset.batch(BATCH_SIZE)

    # Return the dataset.
    return dataset

def load_data_predict(receipt_json, x1='isNumeric', x2='isTotal', x3='xLeftRel', x4='yTopRel'):
    """A function to convert a JSON to a dictionary"""
    
    predict = pd.DataFrame.from_dict(eval(receipt_json), orient='columns')
    predict_x1 = predict.pop(x1)
    predict_x2 = predict.pop(x2)
    predict_x3 = predict.pop(x3)
    predict_x4 = predict.pop(x4)

    predict_data = {x1:predict_x1, x2:predict_x2, x3:predict_x3, x4:predict_x4}
    return predict_data

def predict_input_fn(features):
    """An input function for prediction. It loads data from a dictionary and returns a Tensorflow Dataset"""

    # Convert the inputs to a Dataset.
    dataset = tf.data.Dataset.from_tensor_slices(dict(features))
    dataset = dataset.batch(BATCH_SIZE)

    # Return the dataset.
    return dataset

parser = argparse.ArgumentParser()
parser.add_argument('--reciept_json', type=str, help='json data of receipt to run prediction on')

FEATURES = ['isNumeric', 'isTotal', 'xLeftRel', 'yTopRel']
CATEGORIES = ['vendor name', 'vendor address', 'other', 'item name', 'item price', 'total', 'total price']
STEPS = 1000
BATCH_SIZE = 100

def main(argv):
    
    args = parser.parse_args(argv[1:])
   
    receipt_json = args.reciept_json

    # Feature columns describe how to use the input.
    my_feature_columns = [] 
    for feature_name in FEATURES:
        my_feature_columns.append(tf.feature_column.numeric_column(key=feature_name))

    # Build 2 hidden layer DNN with 10, 10 units respectively.
    classifier = tf.estimator.DNNClassifier(
        feature_columns=my_feature_columns,
        # Two hidden layers of 10 nodes each.
        hidden_units=[10, 10],
        # The model must choose between 7 classes.
        n_classes=7)

    # Train the Model. input_fn = function which loads training data from csv to 
    classifier.train(input_fn=train_input_fn, steps=STEPS)

    # Evaluate the model.
    eval_result = classifier.evaluate(input_fn=eval_input_fn)

    print('\nTest set accuracy: {accuracy:0.3f}\n'.format(**eval_result))
    
    #load data to predict
    predict_data = load_data_predict(receipt_json)

    # Generate predictions from the model
    predictions = classifier.predict(
        input_fn=lambda:predict_input_fn(predict_data))

    prediction = []

    for pred_dict in predictions:
        class_id = pred_dict['class_ids'][0]
        prediction.append(CATEGORIES[class_id])

    predicted_json = json.dumps(prediction)
    print(predicted_json)

if __name__ == '__main__':
    tf.logging.set_verbosity(tf.logging.ERROR)
    tf.app.run(main)


#sample command
#python receipt_estimator_completed.py --reciept_json "[{\"isNumeric\":0,\"isTotal\":0,\"xLeftRel\":12,\"yTopRel\":61},{\"isNumeric\":0,\"isTotal\":0,\"xLeftRel\":12,\"yTopRel\":69},{\"isNumeric\":0,\"isTotal\":0,\"xLeftRel\":12,\"yTopRel\":78},{\"isNumeric\":0,\"isTotal\":1,\"xLeftRel\":12,\"yTopRel\":90},{\"isNumeric\":1,\"isTotal\":0,\"xLeftRel\":72,\"yTopRel\":61},{\"isNumeric\":1,\"isTotal\":0,\"xLeftRel\":75,\"yTopRel\":69},{\"isNumeric\":1,\"isTotal\":0,\"xLeftRel\":75,\"yTopRel\":78},{\"isNumeric\":1,\"isTotal\":0,\"xLeftRel\":70,\"yTopRel\":90},{\"isNumeric\":0,\"isTotal\":0,\"xLeftRel\":26,\"yTopRel\":2},{\"isNumeric\":0,\"isTotal\":0,\"xLeftRel\":32,\"yTopRel\":16},{\"isNumeric\":0,\"isTotal\":0,\"xLeftRel\":24,\"yTopRel\":26},{\"isNumeric\":0,\"isTotal\":0,\"xLeftRel\":19,\"yTopRel\":34},{\"isNumeric\":0,\"isTotal\":0,\"xLeftRel\":30,\"yTopRel\":43}]"


