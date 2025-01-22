<?php
        require_once '../includes/OperacionesBBDD.php';

        $respuesta = array();

        if($_SERVER['REQUEST_METHOD']=='GET'){
                if (isset($_GET['CorreoElectronico']) and isset($_GET['Contrasena'])){
                        $bbdd = new DBOperations();

			$resultado = $bbdd->verificarUsuario($_GET['CorreoElectronico'],$_GET['Contrasena']);
                        if($resultado != 0){
                                $respuesta['error'] = false;
                                $respuesta['mensaje'] = $resultado;
                        }else{
                                $respuesta['error'] = true;
                                $respuesta['mensaje'] = "Los datos introducidos son incorrectos.";
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
