from .toolkit import *


@input_validator("dummy_algorithm")
@input_validator("another_dummy_algorithm")
def all_files_present(context):
    return context.input_dir.contains("somefile.json")


@input_validator("dummy_algorithm")
def file_content_valid(context):
    try:
        import json
        with context.input_dir.open_file("somefile.json", "r") as json_file:
            json.load(json_file)
        return True
    except Exception as ex:
        print(ex)
        return False

