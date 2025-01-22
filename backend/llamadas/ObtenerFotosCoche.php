<?php
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='POST'){
                if (isset($_POST['IdCoche'])){
                        $bbdd = new DBOperations();

                        $resultado = $bbdd->obtenerFotosCoche($_POST['IdCoche']);
                        if($resultado != 0){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = $resultado;

			$resultado2 = $bbdd->obtenerCoche($_POST['IdCoche']);
			$respuesta['Coche'] = $resultado2;
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "No hay fotos para mostrar.";
                }
                }else{
                        $respuesta['error'] = true;
                        $respuesta['mensaje'] = "Faltan campos obligatorios.";
                }
        }else{
                $respuesta['error'] = true;
                $respuesta['mensaje'] = "Petición no válida.";
        }

echo json_encode($respuesta);
?>
